package com.github.rtyvz.senla.tr.runningtracker.extension

import java.text.SimpleDateFormat
import java.util.*

private const val UTC = "UTC"

fun Long.toDateTime(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Long.toDateTimeWithoutUTCOffset(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(UTC)
    }.format(this)
}