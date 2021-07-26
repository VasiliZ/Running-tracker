package com.github.rtyvz.senla.tr.runningtracker.repository.main

import android.location.Location
import bolts.CancellationTokenSource
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.ERROR
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.OK
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserTracks
import com.github.rtyvz.senla.tr.runningtracker.extension.toUserTracks
import com.github.rtyvz.senla.tr.runningtracker.providers.TasksProvider

class MainRunningRepository {
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

    fun saveTrack(track: TrackEntity, listPoints: List<PointEntity>) {
        TasksProvider.getSaveTrackTask(cancellationToken.token, track, listPoints)
    }

    fun updateTrack(track: TrackEntity) {
        TasksProvider.getUpdateTrackIntoDb(track, cancellationToken.token)
    }
}