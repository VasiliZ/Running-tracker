package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.TrackQueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class InsertTrackIntoDbTask {
    fun insertTracksIntiDb(
        tracksList: List<TrackEntity>,
        cancellationToken: CancellationToken
    ): Task<Unit> {
        return Task.callInBackground({
            TrackQueryObject.insertTracksIntoTable(tracksList)
        }, cancellationToken)
    }
}