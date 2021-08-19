package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.PointsQueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class InsertPointsIntoDbTask {
    fun insertTrackPoints(
        pointsList: List<PointEntity>,
        cancellationToken: CancellationToken
    ): Task<Unit> {
        return Task.callInBackground({
            PointsQueryObject.insertPointsIntoTheTable(pointsList)
        }, cancellationToken)
    }
}