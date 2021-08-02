package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class InsertTackIntoDbTask {
    fun replaceTrackIntoDb(
        track: TrackEntity,
        cancellationToken: CancellationToken
    ): Task<Unit> {
        return Task.callInBackground({
            DBHelper.insertTrack(track)
        }, cancellationToken)
    }
}