package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class SaveTrackResponse(
    @SerializedName("status")
    val status: ResponseStatus,
    @SerializedName("id")
    val remoteTrackId: Long,
    @SerializedName("code")
    val errorCode: String?
)