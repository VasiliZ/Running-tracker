package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.db.AlarmsQueryObject
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity

class SelectNotificationsTask {
    fun getNotifications(): Task<List<AlarmEntity>> {
        return Task.callInBackground {
            AlarmsQueryObject.getNotifications()
        }
    }
}