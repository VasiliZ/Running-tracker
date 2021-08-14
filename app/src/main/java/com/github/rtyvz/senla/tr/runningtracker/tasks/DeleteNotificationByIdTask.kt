package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper

class DeleteNotificationByIdTask {
    fun deleteNotificationById(alarmId: Int) {
        Task.callInBackground {
            DBHelper.deleteNotificationById(alarmId)
        }
    }
}