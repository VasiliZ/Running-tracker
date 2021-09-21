package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface NotificationContract {

    interface ViewNotification : MainContract.View {
        fun showEmptyListMessage()
        fun hideEmptyListMessage()
        fun setData(data: List<AlarmEntity>)
        fun addItem(alarmEntity: AlarmEntity)
        fun updateItem(alarmEntity: AlarmEntity)
        fun updateItem(alarmEntity: AlarmEntity, position: Int)
        fun clearState()
        fun removeItem(position: Int)
        fun checkAdapterItemCount()
    }

    interface PresenterNotification : MainContract.Presenter<ViewNotification> {
        fun getNotificationsFromDb()
        fun createNotificationWork(
            date: Long,
            alarmEntity: AlarmEntity?,
            hour: Int,
            minute: Int,
            title: String
        )

        fun removeNotification(alarmEntity: AlarmEntity, position: Int)
        fun changeNotificationToggle(
            isChecked: Boolean,
            alarmEntity: AlarmEntity,
            adapterPosition: Int,
            hour: Int,
            minute: Int
        )
    }
}