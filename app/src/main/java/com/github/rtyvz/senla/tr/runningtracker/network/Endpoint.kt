package com.github.rtyvz.senla.tr.runningtracker.network

object Endpoint {
    private const val BASE = "lesson-26.php?method="
    const val REGISTER_USER = "${BASE}register"
    const val LOGIN_USER = "${BASE}login"
    const val GET_USER_TRACKS = "${BASE}tracks"
    const val SAVE_USER_TRACK = "${BASE}save"
    const val GET_TRACK_POINTS = "${BASE}points"
}