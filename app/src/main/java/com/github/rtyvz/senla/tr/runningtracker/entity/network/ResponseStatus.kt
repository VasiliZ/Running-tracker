package com.github.rtyvz.senla.tr.runningtracker.entity.network

import com.google.gson.annotations.SerializedName

enum class ResponseStatus(val status: String) {
    @SerializedName("ok")
    OK("ok"),

    @SerializedName("error")
    ERROR("error")
}