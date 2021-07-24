package com.github.rtyvz.senla.tr.runningtracker.tasks

import android.location.Location
import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

class InsertLocationIntoDbTask {
    fun insertLocation(location: Location, beginAt: Long, cancellationToken: CancellationToken) {
        Task.callInBackground({
            DBHelper.insertPointsIntoTable(
                listOf(
                    PointEntity(
                        location.latitude,
                        location.longitude,
                        beginAt
                    )
                )
            )
        }, cancellationToken)
    }
}