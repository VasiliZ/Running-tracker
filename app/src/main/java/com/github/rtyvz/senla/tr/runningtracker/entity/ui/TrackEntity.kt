package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackEntity(
    val id: Long = 0L,
    val beginsAt: Long,
    val time: Long,
    val distance: Int,
    //field for checking sent data to remote server or not
    val isSent: Int = 0,
    var listPoint: List<PointEntity> = emptyList()
) : Parcelable