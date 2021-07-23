package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class PointResponse(
    @SerializedName("status")
    val status: ResponseStatus,
    @SerializedName("points")
    val pointsList: List<Point>,
    @SerializedName("code")
    val errorCode: String?
)
