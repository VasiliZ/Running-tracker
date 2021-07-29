package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)
