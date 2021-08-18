package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject

class DeleteTrackPointsTask {
    fun deletePoints(startRunningTime: Long) {
        QueryObject.deleteTrackPoints(startRunningTime)
    }
}