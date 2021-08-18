package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class InsertTackIntoDbTask {
    fun replaceTrackIntoDb(
        track: TrackEntity
    ): Task<Unit> {
        return Task.callInBackground {
            QueryObject.insertTracksIntoTable(listOf(track))
        }
    }
}