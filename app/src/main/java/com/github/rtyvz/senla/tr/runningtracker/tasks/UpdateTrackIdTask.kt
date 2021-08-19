package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.TrackQueryObject

class UpdateTrackIdTask {
    fun updateTrackId(cancellationToken: CancellationToken, id: Long, beginsAt: Long): Task<Unit> {
        return Task.callInBackground({
            TrackQueryObject.updateTrackIdFromBeginsAt(id, beginsAt)
        }, cancellationToken)
    }
}