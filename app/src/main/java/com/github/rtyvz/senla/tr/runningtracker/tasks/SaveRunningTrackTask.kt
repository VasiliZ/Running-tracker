package com.github.rtyvz.senla.tr.runningtracker.tasks

import android.util.Log
import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.toPoint
import com.github.rtyvz.senla.tr.runningtracker.providers.TasksProvider

class SaveRunningTrackTask {
    fun saveTrack(
        cancellationToken: CancellationToken,
        track: TrackEntity,
        listPoints: List<PointEntity>
    ) {
        val updateTrackIntoDbTask =
            TasksProvider.getUpdateTrackIntoDb(track, cancellationToken)
        val saveTrackOnRemoteServerTask = TasksProvider.getSaveTrackOnRemoteServerTask(
            cancellationToken = cancellationToken,
            track = track, listPoints = listPoints.map {
                it.toPoint()
            }
        )
        Task.whenAll(
            listOf(
                updateTrackIntoDbTask, saveTrackOnRemoteServerTask
            )
        ).continueWithTask({
            if (updateTrackIntoDbTask.isFaulted) {
                Log.d("tag", "kek")
            } else {
                Log.d("tag", "kek")
            }

            if (saveTrackOnRemoteServerTask.isFaulted) {
                Log.d("tag", "error send track request")
            } else {
                TasksProvider.getUpdateTrackIntoDb(
                    TrackEntity(
                        beginsAt = track.beginsAt,
                        time = track.time,
                        distance = track.distance,
                        id = saveTrackOnRemoteServerTask.result.remoteTrackId,
                        isSent = 1
                    ), cancellationToken
                )
                Log.d("tag", "${saveTrackOnRemoteServerTask.result.remoteTrackId}")
            }
            return@continueWithTask it
        }, Task.UI_THREAD_EXECUTOR)
    }
}