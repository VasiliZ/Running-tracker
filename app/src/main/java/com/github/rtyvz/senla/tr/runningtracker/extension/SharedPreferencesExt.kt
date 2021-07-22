package com.github.rtyvz.senla.tr.runningtracker.extension

import android.content.SharedPreferences

fun SharedPreferences.putString(key: String, value: String) {
    this.edit().putString(key, value).apply()
}