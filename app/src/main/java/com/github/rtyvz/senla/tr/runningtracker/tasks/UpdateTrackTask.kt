package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class UpdateTrackTask {
    fun updateTrack(trackEntity: TrackEntity, cancellationToken: CancellationToken): Task<Unit> {
        return Task.callInBackground({
            DBHelper.updateTrack(trackEntity)
        }, cancellationToken)
    }
}