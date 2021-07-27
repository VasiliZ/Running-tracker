package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointEntity(
    val lat: Double,
    val lng: Double,
    val beginAt: Long
):Parcelable
