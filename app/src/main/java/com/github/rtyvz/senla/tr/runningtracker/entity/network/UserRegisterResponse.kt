package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class UserRegisterResponse(
    @SerializedName("status")
    val status: ResponseStatus,
    @SerializedName("token")
    val token: String,
    @SerializedName("code")
    val errorCode: String
)