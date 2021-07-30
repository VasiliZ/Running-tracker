package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class UpdateTackIntoDbTask {
    fun replaceTrackIntoDb(
        trackEntity: TrackEntity,
        cancellationToken: CancellationToken
    ): Task<Unit> {
        return Task.callInBackground({
            DBHelper.replaceTrackIntoTable(trackEntity)
        }, cancellationToken)
    }
}