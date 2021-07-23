package com.github.rtyvz.senla.tr.runningtracker.repository.main

import bolts.CancellationTokenSource
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.ERROR
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus.OK
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Track
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
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

    fun insertTracksIntoDB(tracksList: List<Track>) {
        TasksProvider.getInsertTrackIntoDbkTask(cancellationToken.token, tracksList)
    }
}