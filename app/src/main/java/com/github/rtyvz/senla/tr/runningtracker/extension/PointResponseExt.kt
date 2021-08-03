package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.CurrentTrackPoints

fun PointResponse.toCurrentTrackPoints(timeStamp: Long): CurrentTrackPoints {
    return CurrentTrackPoints(this.pointsList.map {
        it.toPointEntity(0)
    })
}