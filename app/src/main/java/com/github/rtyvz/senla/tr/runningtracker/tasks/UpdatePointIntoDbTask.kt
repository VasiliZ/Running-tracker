package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class UpdatePointIntoDbTask {
    fun updatePointIntoDb(cancellationToken: CancellationToken, pointEntity: PointEntity) {
        Task.callInBackground({
            DBHelper.updatePointIntoTable(pointEntity)
        }, cancellationToken)
    }
}