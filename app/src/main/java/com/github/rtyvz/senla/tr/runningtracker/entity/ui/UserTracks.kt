package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Track
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserTracks(
    val tracksList: List<Track>
) : Parcelable