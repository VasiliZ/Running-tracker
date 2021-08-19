package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.Track
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

private const val IS_SENT_FLAG = 1

fun Track.toTrackEntity() = TrackEntity(id, beginsAt, time, distance = distance)

fun Track.toSentTrackEntity() = TrackEntity(id, beginsAt, time, distance, IS_SENT_FLAG)
