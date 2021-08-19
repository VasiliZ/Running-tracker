package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.PointsQueryObject

class DeleteTrackPointsTask {
    fun deletePoints(startRunningTime: Long) {
        PointsQueryObject.deleteTrackPoints(startRunningTime)
    }
}