package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class ReplacePointIntoDbTask {
    fun updatePointIntoDb(cancellationToken: CancellationToken, pointsList: List<PointEntity>) {
        Task.callInBackground({
            DBHelper.replacePointsIntoTheTable(pointsList)
        }, cancellationToken)
    }
}