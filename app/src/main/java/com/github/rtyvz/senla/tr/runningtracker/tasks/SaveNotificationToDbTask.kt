package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity

class SaveNotificationToDbTask {
    fun saveNotificationToDb(alarmEntity: AlarmEntity) {
        Task.callInBackground {
            DBHelper.insertNotificationToDb(alarmEntity)
        }
    }
}