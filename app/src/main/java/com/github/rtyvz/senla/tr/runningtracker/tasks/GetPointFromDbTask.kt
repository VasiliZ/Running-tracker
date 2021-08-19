package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.PointsQueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class GetPointFromDbTask {
    fun getPointsFromDb(
        beginsAt: Long,
        cancellationToken: CancellationToken
    ): Task<List<PointEntity>> {
        return Task.callInBackground({
            PointsQueryObject.getTrackPointsFromDB(beginsAt)
        }, cancellationToken)
    }
}