package com.github.rtyvz.senla.tr.runningtracker.ui.notification.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.NotificationWorkManager
import java.util.*

class NotificationPresenter : BasePresenter<NotificationContract.ViewNotification>(),
    NotificationContract.PresenterNotification {

    companion object {
        private const val IS_ENABLE_NOTIFICATION_FLAG = 1
        private const val IS_DiSABLE_NOTIFICATION_FLAG = 0
    }

    override fun getNotificationsFromDb() {
        App.notificationRepository.getNotifications {
            if (it.isEmpty()) {
                getView().showEmptyListMessage()
            } else {
                getView().hideEmptyListMessage()
                getView().setData(it)
            }
        }
    }

    override fun createNotificationWork(
        date: Long,
        alarmEntity: AlarmEntity?,
        hour: Int,
        minute: Int,
        title: String
    ) {
        var innerAlarmEntity: AlarmEntity
        getView().hideEmptyListMessage()
        if (alarmEntity == null) {
            innerAlarmEntity = AlarmEntity(
                Random().nextInt(Int.MAX_VALUE),
                hour,
                minute,
                title,
                date,
                IS_ENABLE_NOTIFICATION_FLAG
            )

            innerAlarmEntity.let { settings ->
                NotificationWorkManager().createWorkForNotification(settings)
                App.notificationRepository.saveNotificationInDb(settings)
                getView().addItem(settings)

            }
        } else {
            alarmEntity.let { entity ->
                //remove old settings for current notify
                //because task does't trigger if was been cancel
                NotificationWorkManager().deleteWork(entity.alarmId.toString())
                App.notificationRepository.deleteNotification(entity)

                innerAlarmEntity = AlarmEntity(
                    Random().nextInt(Int.MAX_VALUE),
                    hour,
                    minute,
                    title,
                    date,
                    IS_ENABLE_NOTIFICATION_FLAG
                )
                innerAlarmEntity.let {
                    if (entity.isEnabled == IS_ENABLE_NOTIFICATION_FLAG) {
                        NotificationWorkManager().createWorkForNotification(it)
                    }
                    App.notificationRepository.saveNotificationInDb(it)
                    getView().updateItem(it)
                }
            }

            getView().clearState()
        }
    }

    override fun removeNotification(alarmEntity: AlarmEntity, position: Int) {
        NotificationWorkManager().deleteWork(alarmEntity.alarmId.toString())
        App.notificationRepository.deleteNotification(alarmEntity)
        getView().removeItem(position)
        getView().checkAdapterItemCount()
    }

    override fun changeNotificationToggle(
        isChecked: Boolean,
        alarmEntity: AlarmEntity,
        adapterPosition: Int, hour: Int, minute: Int
    ) {
        when (isChecked) {
            true -> {
                val newEntity = alarmEntity.copy(
                    alarmId = Random().nextInt(Int.MAX_VALUE),
                    hour = hour,
                    minute = minute,
                    isEnabled = 1,
                    oldId = alarmEntity.alarmId
                )
                getView().updateItem(newEntity, adapterPosition)
                NotificationWorkManager().createWorkForNotification(newEntity)
                App.notificationRepository.updateNotification(newEntity)
            }
            else -> {
                val newAlarm = alarmEntity.copy(
                    isEnabled = IS_DiSABLE_NOTIFICATION_FLAG,
                    oldId = alarmEntity.alarmId
                )
                getView().updateItem(newAlarm, adapterPosition)
                App.notificationRepository.updateNotification(newAlarm)
                NotificationWorkManager().deleteWork(alarmEntity.alarmId.toString())
            }
        }
    }
}