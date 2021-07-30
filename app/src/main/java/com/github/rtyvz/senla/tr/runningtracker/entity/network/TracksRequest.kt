package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class TracksRequest(
    @SerializedName("token")
    val token: String?
)
