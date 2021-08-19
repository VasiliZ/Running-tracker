package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.PointsQueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class InsertPointIntoDbTask {
    fun insertPoint(point: PointEntity) {
        Task.callInBackground {
            PointsQueryObject.insertPoint(point)
        }
    }
}