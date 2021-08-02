package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleLocation(
    val lat: Double,
    val lng: Double
) : Parcelable