package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.google.android.gms.maps.model.LatLng

fun PointEntity.toPoint(): Point {
    return Point(lat, lng)
}

fun PointEntity.toLatLng(): LatLng {
    return LatLng(lat, lng)
}