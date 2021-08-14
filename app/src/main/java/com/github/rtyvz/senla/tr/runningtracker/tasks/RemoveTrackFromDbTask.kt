package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper

class RemoveTrackFromDbTask {
    fun removeTrackFromDb(conditionForDelete: String) {
        Task.callInBackground {
            DBHelper.deleteTrackFromDb(conditionForDelete)
        }
    }
}