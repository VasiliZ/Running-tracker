package com.github.rtyvz.senla.tr.runningtracker.repository.main

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import bolts.CancellationTokenSource
import bolts.Continuation
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointsRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.ERROR
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.OK
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
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
                        //                 OK -> callback(Result.Success(it.result.toUserTracksSortedDesc()))
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
            }, Task.BACKGROUND_EXECUTOR).continueWith({
                if (!it.isFaulted && it.result.tracks.isNotEmpty()) {
                    val userToken =
                        App.instance.getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)
                    if (userToken != null && userToken.isNotBlank()) {
                        it.result.tracks.forEach { getTrack ->
                            Log.d("get token", getTrack.beginsAt.toString())
                            TasksProvider.getPointsFromServerTask(
                                cancellationToken.token, PointsRequest(
                                    userToken, getTrack.id
                                )
                            ).continueWith {
                                TasksProvider.getInsertLocationTask(
                                    cancellationToken.token,
                                    it.result.pointsList.map { point ->
                                        point.toPointEntity(getTrack.beginsAt)
                                    })
                            }
                        }
                    }
                }
            }, Task.BACKGROUND_EXECUTOR)
            .continueWithTask(Continuation<Unit?, Task<List<TrackEntity>>> {
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
            ).continueWith {
                if (it.isFaulted) {
                    callback(Result.Error(it.error.toString()))
                } else {
                    when (it.result.status) {
                        OK -> callback(Result.Success(it.result.toCurrentTrackPoints()))
                        ERROR -> callback(Result.Error(it.result.errorCode.toString()))
                    }
                }
            }
        }
    }

    fun insertTracksIntoDB(tracksList: List<TrackEntity>) {
        TasksProvider.getInsertTrackIntoDbkTask(cancellationToken.token, tracksList)
    }

    fun insertLocationIntoDb(pointEntitiesList: List<PointEntity>) {
        TasksProvider.getInsertLocationTask(cancellationToken.token, pointEntitiesList)
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
        TasksProvider.getPointsFromDbTask(cancellationToken.token, beginsAt).continueWith {
            if (it.isFaulted) {
                //todo detailed message
                callback(Result.Error(it.error.toString()))
            } else {
                if (it.result.isNotEmpty()) {
                    callback(Result.Success(CurrentTrackPoints(it.result)))
                }
            }
        }
    }
}