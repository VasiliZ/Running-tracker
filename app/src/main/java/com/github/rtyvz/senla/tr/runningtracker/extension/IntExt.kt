package com.github.rtyvz.senla.tr.runningtracker.extension

fun Int.humanizeDistance(): String {
    var postfix = ""
    this.toString().findLast {
        when (it) {
            '0', '5', '6', '7', '8', '9' -> {
                postfix = "ов"
                true
            }
            '2', '3', '4' -> {
                postfix = "а"
                true
            }

            else -> {
                true
            }

        }
    }
    return postfix
}