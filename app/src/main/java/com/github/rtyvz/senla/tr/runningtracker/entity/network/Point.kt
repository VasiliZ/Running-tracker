package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("lat")
    val lat: Double
)
