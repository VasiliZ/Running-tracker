package com.github.rtyvz.senla.tr.runningtracker.extension

import android.location.Location
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

fun Location.toPointEntity(beginsAt: Long): PointEntity {
    return PointEntity(
        latitude,
        longitude,
        beginsAt
    )
}