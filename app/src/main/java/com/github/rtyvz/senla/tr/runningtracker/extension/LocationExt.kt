package com.github.rtyvz.senla.tr.runningtracker.extension

import android.location.Location
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

fun Location.toPointEntity(beginsAt: Long) = PointEntity(
    latitude,
    longitude,
    beginsAt
)