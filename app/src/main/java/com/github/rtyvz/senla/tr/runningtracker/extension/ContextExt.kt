package com.github.rtyvz.senla.tr.runningtracker.extension

import android.content.Context
import android.content.SharedPreferences

private const val RUNNING_SHARED_PREFERENCE_STORAGE = "RUNNING_SHARED_PREFERENCE_STORAGE"

fun Context.getSharedPreference(): SharedPreferences {
    return this.getSharedPreferences(RUNNING_SHARED_PREFERENCE_STORAGE, Context.MODE_PRIVATE)
}