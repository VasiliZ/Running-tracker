package com.github.rtyvz.senla.tr.runningtracker.entity.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDataRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val name: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("password")
    val password: String
) : Parcelable