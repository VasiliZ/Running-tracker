package com.github.rtyvz.senla.tr.runningtracker.repository.main

import android.content.Intent
import android.location.Location
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import bolts.CancellationTokenSource
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.ERROR
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.OK
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserTracks
import com.github.rtyvz.senla.tr.runningtracker.extension.toPoint
import com.github.rtyvz.senla.tr.runningtracker.extension.toUserTracks
import com.github.rtyvz.senla.tr.runningtracker.providers.TasksProvider
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity

class MainRunningRepository {

    companion object {
        private const val INVALID_TOKEN = "INVALID_TOKEN"
        private const val NO_POINTS = "NO_POINTS"
        private const val IS_DATA_SENT_FLAG = 1
        private const val USER_TOKEN = "USER_TOKEN"
    }

    private val cancellationToken = CancellationTokenSource()

    fun getTracksFromNetwork(
        tracksRequest: TracksRequest,
        callback: (Result<UserTracks>) -> (Unit)
    ) {
        TasksProvider.getFetchingTrackFromNetworkTask(tracksRequest, cancellationToken.token)
            .continueWith({
                if (it.isFaulted) {
                    callback(Result.Error(it.error.toString()))
                } else {
                    when (it.result.status) {
                        OK -> callback(Result.Success(it.result.toUserTracks()))
                        ERROR -> callback(Result.Error(it.result.errorCode.toString()))
                    }
                }
            }, Task.UI_THREAD_EXECUTOR)
    }

    fun insertTracksIntoDB(tracksList: List<TrackEntity>) {
        TasksProvider.getInsertTrackIntoDbkTask(cancellationToken.token, tracksList)
    }

    fun insertLocationIntoDb(location: Location, beginAt: Long) {
        TasksProvider.getInsertLocationTask(cancellationToken.token, location, beginAt)
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
}