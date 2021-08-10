package com.github.rtyvz.senla.tr.runningtracker.entity

import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

data class State(
    var lastOpenedUserTrack: TrackEntity? = null,
    var firstVisibleItemPosition: Int = 0,
    var listTracks: List<TrackEntity> = mutableListOf()
)
