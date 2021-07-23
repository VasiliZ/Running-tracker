package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TrackResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest

class FetchTracksFromNetworkTask {
    fun fetchTracks(
        tracksRequest: TracksRequest,
        cancellationToken: CancellationToken
    ): Task<TrackResponse> {
        return Task.callInBackground({
            App.api.getUserTracks(tracksRequest).execute().body()
        }, cancellationToken)
    }
}