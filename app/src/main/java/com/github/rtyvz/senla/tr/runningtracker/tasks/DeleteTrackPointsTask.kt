package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper

class DeleteTrackPointsTask {
    fun deletePoints(startRunningTime: Long) {
        DBHelper.deleteTrackPoints(startRunningTime)
    }
}