package com.github.rtyvz.senla.tr.runningtracker.entity.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Result<out T : Parcelable> : Parcelable {
    @Parcelize
    data class Success<out T : Parcelable>(val data: T) : Result<T>(), Parcelable

    @Parcelize
    data class Error(val error: String) : Result<Nothing>(), Parcelable
}