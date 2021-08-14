package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper

class UpdateTrackIdTask {
    fun updateTrackId(cancellationToken: CancellationToken, id: Long, beginsAt: Long): Task<Unit> {
        return Task.callInBackground({
            DBHelper.updateTrackIdFromBeginsAt(id, beginsAt)
        }, cancellationToken)
    }
}