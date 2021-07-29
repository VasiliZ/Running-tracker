package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class GetPointFromDbTask {
    fun getPointsFromDb(
        beginsAt: Long,
        cancellationToken: CancellationToken
    ): Task<List<PointEntity>> {
        return Task.callInBackground({
            DBHelper.getTrackPointsFromDB(beginsAt)
        }, cancellationToken)
    }
}