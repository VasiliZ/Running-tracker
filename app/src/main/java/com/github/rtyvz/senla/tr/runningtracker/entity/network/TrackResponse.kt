package com.github.rtyvz.senla.tr.runningtracker.entity.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackResponse(
    @SerializedName("status")
    val status: ResponseStatus,
    @SerializedName("tracks")
    val tracks: List<Track>,
    @SerializedName("code")
    val errorCode: String?
) : Parcelable