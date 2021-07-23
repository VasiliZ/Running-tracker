package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackEntity(
    val id: Long,
    val beginsAt: Long,
    val time: Long,
    val distance: Int
) : Parcelable