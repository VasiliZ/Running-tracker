package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class GetUnsentTracksTask {
    fun getUnsentTracks(cancellationToken: CancellationToken): Task<List<TrackEntity>> {
        return Task.callInBackground({
            DBHelper.getUnsentTracksFromDb()
        }, cancellationToken)
    }
}