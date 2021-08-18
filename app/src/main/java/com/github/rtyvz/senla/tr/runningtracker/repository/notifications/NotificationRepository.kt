package com.github.rtyvz.senla.tr.runningtracker.repository.notifications

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.github.rtyvz.senla.tr.runningtracker.providers.TasksProvider

class NotificationRepository {
    fun saveNotificationInDb(alarmEntity: AlarmEntity) {
        TasksProvider.getInsertAlarmTask(alarmEntity)
    }

    fun getNotifications(callback: (List<AlarmEntity>) -> Unit) {
        TasksProvider.getSelectNotificationsTask()
            .onSuccess({
                callback(it.result)
            }, Task.UI_THREAD_EXECUTOR)
    }

    fun updateNotification(alarmEntity: AlarmEntity) {
        TasksProvider.getUpdateNotificationTask(alarmEntity)
    }

    fun deleteNotification(alarmEntity: AlarmEntity) {
        TasksProvider.getDeleteNotificationByIdTask(alarmEntity.alarmId)
    }

    fun updateNotificationEnabledStateById(alarmId: Int, disableNotificationFlag: Int) {
        TasksProvider.getUpdateStateNotificationTask(alarmId, disableNotificationFlag)
    }
}