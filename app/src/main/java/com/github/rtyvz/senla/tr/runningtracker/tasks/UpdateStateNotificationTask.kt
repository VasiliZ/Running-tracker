package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject

class UpdateStateNotificationTask {
    fun updateNotificationState(alarmId: Int, stateFlag: Int) {
        QueryObject.updateNotificationStateById(alarmId, stateFlag)
    }
}