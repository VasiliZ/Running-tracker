package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointsRequest

class GetPointsFromServer {
    fun getPoints(
        cancellationToken: CancellationToken,
        pointsRequest: PointsRequest
    ): Task<PointResponse> {
        return Task.callInBackground({
            App.api.getPoints(pointsRequest).execute().body()
        }, cancellationToken)
    }
}