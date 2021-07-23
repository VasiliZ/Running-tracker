package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.TrackResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserTracks

fun TrackResponse.toUserTracks(): UserTracks {
    return UserTracks(this.tracks.map {
        it.toTrackEntity()
    })
}