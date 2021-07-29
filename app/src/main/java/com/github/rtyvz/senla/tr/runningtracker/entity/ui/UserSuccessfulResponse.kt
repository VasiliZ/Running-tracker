package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserSuccessfulResponse(
    val errorCode: String?
) : Parcelable