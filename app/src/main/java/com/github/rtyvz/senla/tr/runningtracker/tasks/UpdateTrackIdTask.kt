package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject

class UpdateTrackIdTask {
    fun updateTrackId(cancellationToken: CancellationToken, id: Long, beginsAt: Long): Task<Unit> {
        return Task.callInBackground({
            QueryObject.updateTrackIdFromBeginsAt(id, beginsAt)
        }, cancellationToken)
    }
}