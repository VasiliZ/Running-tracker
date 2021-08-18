package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.network.SaveTrackRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.SaveTrackResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference

class SaveTrackOnRemoteServerTask {
    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val EMPTY_STRING = ""
    }

    fun saveTrackOnRemoteServer(
        trackEntity: TrackEntity,
        listPoints: List<Point>,
        cancellationToken: CancellationToken
    ): Task<SaveTrackResponse> {
        return Task.callInBackground({
            App.api.saveTrack(
                SaveTrackRequest(
                    token = App.instance.getRunningSharedPreference()
                        .getString(USER_TOKEN, EMPTY_STRING)
                        ?: EMPTY_STRING,
                    beginAt = trackEntity.beginsAt,
                    time = trackEntity.time,
                    distance = trackEntity.distance,
                    pointList = listPoints
                )
            ).execute().body()
        }, cancellationToken)
    }
}