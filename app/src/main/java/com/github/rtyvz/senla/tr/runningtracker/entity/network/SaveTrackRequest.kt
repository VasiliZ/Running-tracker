package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class SaveTrackRequest(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("token")
    val token: String,

    @SerializedName("beginsAt")
    val beginAt: Long,

    @SerializedName("time")
    val time: Long,

    @SerializedName("distance")
    val distance: Int,

    @SerializedName("points")
    val pointsList: List<Point>
)
