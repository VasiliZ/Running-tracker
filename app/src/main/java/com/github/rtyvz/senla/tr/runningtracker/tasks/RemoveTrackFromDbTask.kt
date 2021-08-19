package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.TrackQueryObject

class RemoveTrackFromDbTask {
    fun removeTrackFromDb(conditionForDelete: String) {
        Task.callInBackground {
            TrackQueryObject.deleteTrackFromDb(conditionForDelete)
        }
    }
}