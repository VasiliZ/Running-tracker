package com.github.rtyvz.senla.tr.runningtracker.tasks

import android.location.Location
import android.util.Log
import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.providers.TasksProvider

class SaveRunningTrackTask {
    fun saveTrack(
        cancellationToken: CancellationToken,
        track: TrackEntity,
        listLocation: List<Location>
    ) {
        val saveTrackIntoDbTask =
            TasksProvider.getInsertTrackIntoDbkTask(cancellationToken, listOf(track))
        val saveTrackOnRemoteServerTask = TasksProvider.getSaveTrackOnRemoteServerTask(
            cancellationToken = cancellationToken,
            track = track, listLocation = listLocation
        )
        Task.whenAll(
            listOf(
                saveTrackIntoDbTask, saveTrackOnRemoteServerTask
            )
        ).continueWithTask({
            if (saveTrackIntoDbTask.isFaulted) {
                Log.d("tag", "kek")
            } else {
                Log.d("tag", "kek")
            }

            if (saveTrackIntoDbTask.isFaulted) {
                Log.d("tag", "error send track request")
            } else {
                when (saveTrackOnRemoteServerTask.result.errorCode) {

                }
                //update db
                Log.d("tag", "${saveTrackOnRemoteServerTask.result.remoteTrackId}")
            }
            return@continueWithTask it
        }, Task.UI_THREAD_EXECUTOR)
    }
}