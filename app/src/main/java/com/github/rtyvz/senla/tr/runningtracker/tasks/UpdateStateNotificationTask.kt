package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper

class UpdateStateNotificationTask {
    fun updateNotificationState(alarmId: Int, stateFlag: Int) {
        DBHelper.updateNotificationStateById(alarmId, stateFlag)
    }
}