package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class InsertTrackIntoDbTask {
    fun insertTracksIntiDb(
        tracksList: List<TrackEntity>,
        cancellationToken: CancellationToken
    ): Task<Unit> {
        return Task.callInBackground({
            QueryObject.insertTracksIntoTable(tracksList)
        }, cancellationToken)
    }
}