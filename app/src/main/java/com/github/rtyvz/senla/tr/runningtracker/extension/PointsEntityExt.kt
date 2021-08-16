package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

fun Point.toPointEntity(beginsAt: Long): PointEntity {
    return PointEntity(lat, lng, beginsAt)
}