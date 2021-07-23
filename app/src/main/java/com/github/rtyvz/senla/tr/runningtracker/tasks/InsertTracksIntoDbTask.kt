package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Track

class InsertTracksIntoDbTask {
    fun insertTracksIntiDb(tracks: List<Track>, cancellationToken: CancellationToken) {
        Task.callInBackground({
            DBHelper.insertTracksIntoTable(tracks)
        }, cancellationToken)
    }
}