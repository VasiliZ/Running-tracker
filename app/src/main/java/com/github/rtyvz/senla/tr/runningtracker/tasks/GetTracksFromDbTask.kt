package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class GetTracksFromDbTask {
    fun getTracksFromDb(cancellationToken: CancellationToken): Task<List<TrackEntity>> {
        return Task.callInBackground({
            DBHelper.getTracksFromDb()
        }, cancellationToken)
    }
}