package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper

class UpdateStateNotificationTask {
    fun updateNotificationState(alarmId: Int, stateFlag: Int) {
        DBHelper.updateNotificationStateById(alarmId, stateFlag)
    }
}