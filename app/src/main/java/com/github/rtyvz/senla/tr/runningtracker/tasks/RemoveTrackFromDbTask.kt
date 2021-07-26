package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper

class RemoveTrackFromDbTask {
    fun removeTrackFromDb(cancellationToken: CancellationToken, conditionForDelete: String) {
        Task.callInBackground({
            DBHelper.deleteTrackFromDb(conditionForDelete)
        }, cancellationToken)
    }
}