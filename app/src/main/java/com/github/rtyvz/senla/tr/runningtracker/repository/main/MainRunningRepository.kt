package com.github.rtyvz.senla.tr.runningtracker.repository.main

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import bolts.CancellationTokenSource
import bolts.Continuation
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
        TasksProvider.getFetchingTrackFromNetworkTask(tracksRequest, cancellationToken.token)
            .continueWithTask({
                if (it.isFaulted) {
                    callback(Result.Error(it.error.toString()))
                } else {
                    when (it.result.status) {
                        OK -> callback(Result.Success(it.result.toUserTracksSortedDesc()))
                        ERROR -> callback(Result.Error(it.result.errorCode.toString()))
                    }
                }
                return@continueWithTask it
            }, Task.UI_THREAD_EXECUTOR)
            .continueWithTask({
                if (!it.isFaulted && it.result.tracks.isNotEmpty()) {
                    DBHelper.insertTracksIntoTable(
                        it.result.tracks.map { track ->
                            track.toSentTrackEntity()
                        }
                    )
                }
                return@continueWithTask it
            }, Task.BACKGROUND_EXECUTOR).continueWithTask({

                if (!it.isFaulted && it.result.tracks.isNotEmpty()) {
                    val userToken =
                        App.instance.getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)
                    if (userToken != null && userToken.isNotBlank()) {
                        it.result.tracks.forEach { getTrack ->
                            Log.d("get token", getTrack.beginsAt.toString())
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
                if (it.isFaulted) {
                    //todo error can't load points for track
                } else {
                    listTask.forEach { map ->
                        if (!it.isFaulted) {
                            TasksProvider.getInsertTrackPointsTask(
                                cancellationToken.token,
                                map.value.result.pointsList.map { point ->
                                    point.toPointEntity(map.key)
                                })
                        }
                    }
                }
                return@onSuccess null
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask(Continuation<Nothing?, Task<List<TrackEntity>>> {
                return@Continuation TasksProvider.getTracksFromDb(cancellationToken.token)
            }, Task.BACKGROUND_EXECUTOR)
            .continueWith({
                if (!it.isFaulted && it.result.isNotEmpty()) {
                    callback(Result.Success(UserTracks(it.result.sortedByDescending { trackEntity ->
                        trackEntity.beginsAt
                    })))
                }
            }, Task.UI_THREAD_EXECUTOR)
    }

    fun getTrackPoints(remoteTrackId: Long, callback: (Result<CurrentTrackPoints>) -> Unit) {
        val userToken = App.instance.getSharedPreference().getString(
            USER_TOKEN, EMPTY_STRING
        )
        if (userToken != null && userToken.isNotBlank()) {
            TasksProvider.getPointsFromServerTask(
                cancellationToken.token,
                PointsRequest(userToken, remoteTrackId)
            ).continueWith({
                if (it.isFaulted) {
                    callback(Result.Error(it.error.toString()))
                } else {
                    when (it.result.status) {
                        OK -> callback(Result.Success(it.result.toCurrentTrackPoints()))
                        ERROR -> callback(Result.Error(it.result.errorCode.toString()))
                    }
                }
            }, Task.UI_THREAD_EXECUTOR)
        }
    }

    fun insertTracksIntoDB(tracksList: List<TrackEntity>) {
        TasksProvider.getInsertTrackIntoDbkTask(cancellationToken.token, tracksList)
    }

    fun insertLocationIntoDb(pointEntitiesList: List<PointEntity>) {
        TasksProvider.getInsertTrackPointsTask(cancellationToken.token, pointEntitiesList)
    }

    fun saveTrack(
        track: TrackEntity,
        listPoints: List<PointEntity>
    ) {
        val updateTrackIntoDbTask =
            TasksProvider.getUpdateTrackIntoDb(track, cancellationToken.token)
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
                        TasksProvider.getUpdateTrackIntoDb(
                            TrackEntity(
                                beginsAt = track.beginsAt,
                                time = track.time,
                                distance = track.distance,
                                isSent = IS_DATA_SENT_FLAG,
                                id = saveTrackOnRemoteServerTask.result.remoteTrackId
                            ), cancellationToken.token
                        ).continueWith({ updateTrackTask ->
                            if (updateTrackTask.isFaulted) {
                                LocalBroadcastManager.getInstance(App.instance).sendBroadcastSync(
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

    fun getPointsFromDb(beginsAt: Long, callback: (Result<CurrentTrackPoints>) -> Unit) {
        TasksProvider.getPointsFromDbTask(cancellationToken.token, beginsAt)
            .continueWith({
                if (it.isFaulted) {
                    //todo detailed message
                    callback(Result.Error(it.error.toString()))
                } else {
                    if (it.result.isNotEmpty()) {
                        callback(Result.Success(CurrentTrackPoints(it.result)))
                    }
                }
            }, Task.UI_THREAD_EXECUTOR)
    }

    fun getTracksFromDb(callback: (Result<UserTracks>) -> Unit) {
        val mapPointTask = mutableMapOf<Long, Task<PointResponse>>()
        val mapUnsentTask = mutableMapOf<Long, Task<SaveTrackResponse>>()
        val updatedTrackTasks = mutableMapOf<Long, Task<Unit>>()
        TasksProvider.getTracksFromDb(cancellationToken.token).continueWith({
            if (it.result.isNotEmpty()) {
                callback(Result.Success(UserTracks(it.result.sortedByDescending { track ->
                    track.beginsAt
                })))
            }
            return@continueWith it.result
        }, Task.UI_THREAD_EXECUTOR)
            .continueWithTask({
                return@continueWithTask TasksProvider.getFetchingTrackFromNetworkTask(
                    TracksRequest(
                        App.instance.getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)
                    ),
                    cancellationToken.token
                )
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
                val data = it.result
                if (data != null && !it.isFaulted) {
                    data.tracks.forEach { track ->
                        updatedTrackTasks[track.beginsAt] = TasksProvider.getUpdateTrackIntoDb(
                            track.toSentTrackEntity(), cancellationToken.token
                        )
                    }
                } else {
                    callback(Result.Error(it.result?.errorCode.toString()))
                }
                return@continueWithTask it
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
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
                mapPointTask.forEach { tasksMap ->
                    if (!tasksMap.value.isFaulted) {
                        tasksMap.value.result.pointsList.forEach { point ->
                            TasksProvider.getUpdatePointsIntoDbTask(
                                cancellationToken.token,
                                point.toPointEntity(tasksMap.key)
                            )
                        }
                    }
                }
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
                return@continueWithTask TasksProvider.getUnsentTracks(cancellationToken.token)
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask({
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
}