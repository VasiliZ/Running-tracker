package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper

class UpdateTrackIdTask {
    fun updateTrackId(cancellationToken: CancellationToken, id: Long, beginsAt: Long) {
        Task.callInBackground({
            DBHelper.updateTrackIdFromBeginsAt(id, beginsAt)
        }, cancellationToken)
    }
}