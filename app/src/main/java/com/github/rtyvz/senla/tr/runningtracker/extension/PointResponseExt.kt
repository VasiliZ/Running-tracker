package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.CurrentTrackPoints

private const val DEFAULTS_BEGINS_AT_TIME = 0L

fun PointResponse.toCurrentTrackPoints() = CurrentTrackPoints(this.pointsList.map {
    it.toPointEntity(DEFAULTS_BEGINS_AT_TIME)
})
