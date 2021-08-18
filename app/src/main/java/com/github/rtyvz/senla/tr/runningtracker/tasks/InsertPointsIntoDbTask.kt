package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class InsertPointsIntoDbTask {
    fun insertTrackPoints(
        pointsList: List<PointEntity>,
        cancellationToken: CancellationToken
    ): Task<Unit> {
        return Task.callInBackground({
            QueryObject.insertPointsIntoTheTable(pointsList)
        }, cancellationToken)
    }
}