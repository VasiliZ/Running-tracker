package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class ReplaceTrackIntoDbTask {
    fun replaceTracksIntiDb(
        tracksList: List<TrackEntity>,
        cancellationToken: CancellationToken
    ): Task<Unit> {
        return Task.callInBackground({
            DBHelper.replaceTrackIntoTable(tracksList)
        }, cancellationToken)
    }
}