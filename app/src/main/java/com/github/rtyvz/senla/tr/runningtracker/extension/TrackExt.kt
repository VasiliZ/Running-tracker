package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.Track
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

fun Track.toTrackEntity():TrackEntity{
    return TrackEntity(id, beginsAt, time, distance)
}