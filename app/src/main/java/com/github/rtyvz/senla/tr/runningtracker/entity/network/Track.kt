package com.github.rtyvz.senla.tr.runningtracker.entity.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    @SerializedName("id")
    val id: Long,
    @SerializedName("beginsAt")
    val beginsAt: Long,
    @SerializedName("time")
    val time: Long,
    @SerializedName("distance")
    val distance: Int
) : Parcelable