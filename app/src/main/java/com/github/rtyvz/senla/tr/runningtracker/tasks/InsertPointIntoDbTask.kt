package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class InsertPointIntoDbTask {
    fun insertPoint(point: PointEntity) {
        Task.callInBackground {
            DBHelper.insertPoint(point)
        }
    }
}