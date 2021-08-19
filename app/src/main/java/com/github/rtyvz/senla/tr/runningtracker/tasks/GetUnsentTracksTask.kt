package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.TrackQueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class GetUnsentTracksTask {
    fun getUnsentTracks(cancellationToken: CancellationToken): Task<List<TrackEntity>> {
        return Task.callInBackground({
            TrackQueryObject.getUnsentTracksFromDb()
        }, cancellationToken)
    }
}