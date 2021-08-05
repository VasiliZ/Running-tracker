package com.github.rtyvz.senla.tr.runningtracker.entity

import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

data class State(
    var lastOpenedUserTrack: TrackEntity? = null
)
