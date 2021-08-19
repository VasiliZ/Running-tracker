package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.AlarmsQueryObject

class DeleteNotificationByIdTask {
    fun deleteNotificationById(alarmId: Int) {
        Task.callInBackground {
            AlarmsQueryObject.deleteNotificationById(alarmId)
        }
    }
}