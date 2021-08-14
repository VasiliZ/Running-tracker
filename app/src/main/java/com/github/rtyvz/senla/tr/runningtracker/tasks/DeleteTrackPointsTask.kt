package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper

class DeleteTrackPointsTask {
    fun deletePoints(startRunningTime: Long) {
        DBHelper.deleteTrackPoints(startRunningTime)
    }
}