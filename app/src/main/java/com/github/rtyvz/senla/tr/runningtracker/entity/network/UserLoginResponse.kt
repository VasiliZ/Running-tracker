package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

data class UserLoginResponse(
    @SerializedName("status")
    val status: ResponseStatus,
    @SerializedName("token")
    val token: String,
    @SerializedName("firstName")
    val name: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("code")
    val errorCode: String
)
