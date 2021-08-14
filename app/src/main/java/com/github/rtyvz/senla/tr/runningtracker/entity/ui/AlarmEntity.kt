package com.github.rtyvz.senla.tr.runningtracker.entity.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmEntity(
    val alarmId: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
    val title: String,
    val day: Long = 0L,
    var isEnabled: Int,
    var oldId: Int = 0
) : Parcelable