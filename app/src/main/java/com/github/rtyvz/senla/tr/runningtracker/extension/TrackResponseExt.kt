package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.TrackResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserTracks

fun TrackResponse.toUserTracksSortedDesc(): UserTracks {
    return UserTracks(this.tracks.sortedByDescending {
        it.beginsAt
    }.map {
        it.toTrackEntity()
    })
}