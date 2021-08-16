package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//wrapper for result
@Parcelize
data class CurrentTrackPoints(
    val listPoints: List<PointEntity>
) : Parcelable
