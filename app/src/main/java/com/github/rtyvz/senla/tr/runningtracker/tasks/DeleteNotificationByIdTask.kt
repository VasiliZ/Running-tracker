package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject

class DeleteNotificationByIdTask {
    fun deleteNotificationById(alarmId: Int) {
        Task.callInBackground {
            QueryObject.deleteNotificationById(alarmId)
        }
    }
}