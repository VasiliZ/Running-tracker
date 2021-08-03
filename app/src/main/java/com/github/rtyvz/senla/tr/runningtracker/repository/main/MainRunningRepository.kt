package com.github.rtyvz.senla.tr.runningtracker.repository.main

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import bolts.CancellationTokenSource
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.network.*
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.ERROR
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.OK
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.CurrentTrackPoints
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserTracks
import com.github.rtyvz.senla.tr.runningtracker.extension.*
import com.github.rtyvz.senla.tr.runningtracker.providers.TasksProvider
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity

class MainRunningRepository {

    companion object {
        private const val INVALID_TOKEN = "INVALID_TOKEN"
        private const val NO_POINTS = "NO_POINTS"
        private const val IS_DATA_SENT_FLAG = 1
        private const val USER_TOKEN = "USER_TOKEN"
        private const val EMPTY_STRING = ""
    }

    private val listTask = mutableMapOf<Long, Task<PointResponse>>()

    private val cancellationToken = CancellationTokenSource()

    fun getTracks(
        tracksRequest: TracksRequest,
        callback: (Result<UserTracks>) -> (Unit)
    ) {
        //get tacks from network
        val insertPointsTasks = mutableMapOf<Long, Task<Unit>>()
        TasksProvider.getFetchingTrackFromNetworkTask(tracksRequest, cancellationToken.token)
            .continueWithTask({
                if (it.isFaulted) {
                    callback(Result.Error(it.error.toString()))
                } else {
                    when (it.result.status) {
                        OK -> {
                            callback(Result.Success(UserTracks(it.result.tracks.sortedByDescending { track ->
                                track.beginsAt
                            }.map { mapTrack ->
                                mapTrack.toTrackEntity()
                            })))
                        }
                        ERROR -> callback(Result.Error(it.result.errorCode.toString()))
                    }
                }
                return@continueWithTask it
            }, Task.UI_THREAD_EXECUTOR)
            .continueWithTask({
                //save tracks into database
                if (!it.isFaulted && it.result.tracks.isNotEmpty()) {
                    DBHelper.insertTracksIntoTable(it.result.tracks.map { track ->
                        track.toSentTrackEntity()
                    })
                }
                return@continueWithTask it
            }, Task.BACKGROUND_EXECUTOR).continueWithTask({
                //get all points for all tracks
                if (!it.isFaulted && it.result.tracks.isNotEmpty()) {
                    val userToken =
                        App.instance.getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)
                    if (userToken != null && userToken.isNotBlank()) {
                        it.result.tracks.forEach { getTrack ->
                            listTask[
                                    getTrack.beginsAt] =
                                TasksProvider.getPointsFromServerTask(
                                    cancellationToken.token, PointsRequest(
                                        userToken, getTrack.id
                                    )
                                )
                        }
                    }
                }
                return@continueWithTask Task.whenAll(listTask.values)
            }, Task.BACKGROUND_EXECUTOR)
            .onSuccess({
                //insert points into table
                if (it.isFaulted) {
                    //todo error can't load points for track
                } else {
                    listTask.forEach { map ->
                        if (!it.isFaulted) {
                            insertPointsTasks[map.key] = TasksProvider.getInsertTrackPointsTask(
                                cancellationToken.token,
                                map.value.result.pointsList.map { point ->
                                    point.toPointEntity(map.key)
                                })
                        }
                    }
                }
                return@onSuccess Task.whenAll(insertPointsTasks.values)
            }, Task.BACKGROUND_EXECUTOR)
            .continueWith({
                //get all data from database
                return@continueWith TasksProvider.getTracksFromDb(cancellationToken.token).result
            }, Task.BACKGROUND_EXECUTOR)
    }

    fun getTrackPoints(
        remoteTrackId: Long,
        beginsAt: Long,
        callback: (Result<CurrentTrackPoints>) -> Unit
    ) {
        val userToken = App.instance.getSharedPreference().getString(
            USER_TOKEN, EMPTY_STRING
        )
        if (userToken != null && userToken.isNotBlank()) {
            TasksProvider.getPointsFromServerTask(
                cancellationToken.token,
                PointsRequest(userToken, remoteTrackId)
            ).continueWithTask({
                if (!it.isFaulted && it.result.pointsList.isNotEmpty()) {
                    when (it.result.status) {
                        OK -> callback(Result.Success(it.result.toCurrentTrackPoints()))
                        ERROR -> callback(Result.Error(it.result.errorCode.toString()))
                    }
                }
                return@continueWithTask it
            }, Task.UI_THREAD_EXECUTOR).continueWith {
                if (it.isFaulted) {
                    TasksProvider.getPointsFromDbTask(cancellationToken.token, beginsAt)
                        .continueWith({ pointsList ->
                            if (pointsList.isFaulted) {
                                callback(Result.Error(pointsList.error.toString()))
                            } else {
                                callback(Result.Success(CurrentTrackPoints(pointsList.result)))
                            }
                        }, Task.UI_THREAD_EXECUTOR)
                }
            }
        }
    }

    fun insertTracksIntoDB(track: TrackEntity) {
        TasksProvider.getInsertTrackTask(track, cancellationToken.token)
    }

    fun insertLocationIntoDb(point: PointEntity) {
        TasksProvider.getInsertPointTask(point, cancellationToken.token)
    }

    fun saveTrack(
        track: TrackEntity,
        listPoints: List<PointEntity>
    ) {
        val updateTrackIntoDbTask =
            TasksProvider.getUpdateTrackTask(track, cancellationToken.token)
        val saveTrackOnRemoteServerTask = TasksProvider.getSaveTrackOnRemoteServerTask(
            cancellationToken = cancellationToken.token,
            track = track, listPoints = listPoints.map {
                it.toPoint()
            }
        )
        Task.whenAll(
            listOf(
                updateTrackIntoDbTask, saveTrackOnRemoteServerTask
            )
        ).continueWithTask({
            if (updateTrackIntoDbTask.isFaulted) {
                LocalBroadcastManager.getInstance(App.instance).sendBroadcastSync(
                    Intent(RunningActivity.BROADCAST_ERROR_SAVE_TRACK_TO_LOCAL_STORAGE)
                )
            }

            if (saveTrackOnRemoteServerTask.isFaulted) {
                LocalBroadcastManager.getInstance(App.instance).sendBroadcastSync(
                    Intent(RunningActivity.BROADCAST_NETWORK_ERROR)
                )
            } else {
                when (saveTrackOnRemoteServerTask.result.errorCode) {
                    INVALID_TOKEN -> {
                        LocalBroadcastManager.getInstance(App.instance).sendBroadcastSync(
                            Intent(RunningActivity.BROADCAST_WRONG_USER_TOKEN)
                        )
                    }
                    NO_POINTS -> {
                        TasksProvider.getDeleteTrackFromDbTask(
                            cancellationToken.token,
                            track.beginsAt.toString()
                        )
                    }
                    null -> {
                        TasksProvider.getUpdateTrackTask(
                            TrackEntity(
                                beginsAt = track.beginsAt,
                                time = track.time,
                                distance = track.distance,
                                isSent = IS_DATA_SENT_FLAG,
                                id = saveTrackOnRemoteServerTask.result.remoteTrackId
                            ), cancellationToken.token
                        ).continueWith({ updateTrackTask ->
                            if (updateTrackTask.isFaulted) {
                                LocalBroadcastManager.getInstance(App.instance)
                                    .sendBroadcastSync(
                                        Intent(RunningActivity.BROADCAST_ERROR_SAVE_TRACK_TO_LOCAL_STORAGE)
                                    )
                            }
                        }, Task.UI_THREAD_EXECUTOR)
                    }
                    else -> {
                        LocalBroadcastManager.getInstance(App.instance).sendBroadcastSync(
                            Intent(RunningActivity.BROADCAST_NETWORK_ERROR)
                        )
                    }
                }
            }
            return@continueWithTask it
        }, Task.UI_THREAD_EXECUTOR)
    }

    fun removeEmptyTrack(beginsAt: Long) {
        TasksProvider.getDeleteTrackFromDbTask(cancellationToken.token, beginsAt.toString())
    }

    fun getTracksFromDb(callback: (Result<UserTracks>) -> Unit) {
        val mapPointTask = mutableMapOf<Long, Task<PointResponse>>()
        val mapUnsentTask = mutableMapOf<Long, Task<SaveTrackResponse>>()
        //get all tracks from database
        TasksProvider.getTracksFromDb(cancellationToken.token).continueWith({
            if (it.result.isNotEmpty()) {
                callback(Result.Success(UserTracks(it.result.sortedByDescending { track ->
                    track.beginsAt
                })))
            }
            return@continueWith it.result
        }, Task.UI_THREAD_EXECUTOR)
            .continueWithTask({
                //send request for all tracks in network
                return@continueWithTask TasksProvider.getFetchingTrackFromNetworkTask(
                    TracksRequest(
                        App.instance.getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)
                    ),
                    cancellationToken.token
                )
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
                //save all tracks into database
                val data = it.result
                if (data != null && !it.isFaulted) {
                    TasksProvider.getInsertTracksIntoDbkTask(
                        cancellationToken.token,
                        data.tracks.map { track ->
                            track.toSentTrackEntity()
                        })
                } else {
                    callback(Result.Error(it.result?.errorCode.toString()))
                }
                return@continueWithTask it
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
                //send request for all points for all tracks
                val userToken =
                    App.instance.getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)
                if (userToken != null && userToken.isNotBlank()) {
                    it.result?.tracks?.forEach { track ->
                        mapPointTask[track.beginsAt] = TasksProvider.getPointsFromServerTask(
                            cancellationToken.token,
                            PointsRequest(userToken, track.id)
                        )
                    }
                }
                return@continueWithTask Task.whenAll(mapPointTask.values)
            }, Task.BACKGROUND_EXECUTOR)
            .onSuccess({
                //insert all points into the table
                mapPointTask.forEach { tasksMap ->
                    if (!tasksMap.value.isFaulted) {
                        TasksProvider.getInsertTrackPointsTask(
                            cancellationToken.token,
                            tasksMap.value.result.pointsList.map {
                                it.toPointEntity(tasksMap.key)
                            }.sortedBy {
                                it.beginAt
                            })
                    }
                }
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
                //get all unsent tracks
                return@continueWithTask TasksProvider.getUnsentTracks(cancellationToken.token)
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
                //push all tracks on remote server
                it.result.forEach { trackForSend ->
                    mapUnsentTask[trackForSend.beginsAt] =
                        TasksProvider.getSaveTrackOnRemoteServerTask(
                            trackForSend,
                            trackForSend.listPoints.map { pointForSend ->
                                pointForSend.toPoint()
                            },
                            cancellationToken.token
                        )
                }
                return@continueWithTask Task.whenAll(mapUnsentTask.values)
            }, Task.BACKGROUND_EXECUTOR).onSuccess({
                if (it.isFaulted) {
                    callback(Result.Error("Не удалось отправить данные на сервер"))
                } else {
                    //save success request into database after save unsent points
                    mapUnsentTask.forEach { map ->
                        TasksProvider.getUpdateIdTrackTask(
                            map.value.result.remoteTrackId,
                            map.key,
                            cancellationToken.token
                        )
                    }
                }
                return@onSuccess it
            }, Task.BACKGROUND_EXECUTOR)
            .continueWith {
                if (it.isFaulted) {
                    callback(Result.Error("Не удалось отправить данные на сервер"))
                } else {
                    //update ui data from new data in database
                    TasksProvider.getTracksFromDb(cancellationToken.token)
                        .continueWith({
                            if (it.result.isNotEmpty()) {
                                callback(Result.Success(UserTracks(it.result.sortedByDescending { track ->
                                    track.beginsAt
                                })))
                            }
                        }, Task.UI_THREAD_EXECUTOR)
                }
            }
    }

    fun clearCache() {
        TasksProvider.getClearUserDataTask(cancellationToken.token)
    }
}