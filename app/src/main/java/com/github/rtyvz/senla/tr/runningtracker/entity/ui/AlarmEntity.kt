package com.github.rtyvz.senla.tr.runningtracker.entity.ui

data class AlarmEntity(
    val alarmId: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
    val title: String,
    val day: Long = 5000L,
    var isEnabled: Int
)
