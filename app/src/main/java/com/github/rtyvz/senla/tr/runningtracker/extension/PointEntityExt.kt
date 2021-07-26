package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity

fun PointEntity.toPoint(): Point {
    return Point(lng, lat)
}