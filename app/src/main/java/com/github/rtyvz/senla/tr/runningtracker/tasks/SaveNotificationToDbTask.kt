package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.AlarmsQueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity

class SaveNotificationToDbTask {
    fun saveNotificationToDb(alarmEntity: AlarmEntity) {
        Task.callInBackground {
            AlarmsQueryObject.insertNotificationToDb(alarmEntity)
        }
    }
}