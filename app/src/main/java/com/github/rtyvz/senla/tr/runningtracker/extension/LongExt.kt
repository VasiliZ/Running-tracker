package com.github.rtyvz.senla.tr.runningtracker.extension

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateTime(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Long.toDateTimeWithZeroUTC(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(this)
}