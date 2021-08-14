package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

class InsertTackIntoDbTask {
    fun replaceTrackIntoDb(
        track: TrackEntity
    ): Task<Unit> {
        return Task.callInBackground {
            DBHelper.insertTracksIntoTable(listOf(track))
        }
    }
}