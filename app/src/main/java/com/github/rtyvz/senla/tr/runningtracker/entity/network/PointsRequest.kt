package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class PointsRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("id")
    val remoteId: Long
)
