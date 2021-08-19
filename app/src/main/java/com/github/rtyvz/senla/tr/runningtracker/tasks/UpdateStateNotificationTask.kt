package com.github.rtyvz.senla.tr.runningtracker.tasks

import com.github.rtyvz.senla.tr.runningtracker.db.AlarmsQueryObject

class UpdateStateNotificationTask {
    fun updateNotificationState(alarmId: Int, stateFlag: Int) {
        AlarmsQueryObject.updateNotificationStateById(alarmId, stateFlag)
    }
}