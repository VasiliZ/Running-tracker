package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorkManager {

    companion object {
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_ALARM_ID = "ALARM_ID"
        const val EXTRA_GOAL_TIME = "GOAL_TIME"
    }

    fun createWorkForNotification(alarmEntity: AlarmEntity) {
        val data = Data.Builder().apply {
            putString(EXTRA_TITLE, alarmEntity.title)
            putInt(EXTRA_ALARM_ID, alarmEntity.alarmId)
            putLong(EXTRA_GOAL_TIME, getGoalTime(alarmEntity).timeInMillis)
        }
        val duration = getGoalTime(alarmEntity).timeInMillis - Calendar.getInstance().timeInMillis
        val dailyWorkerRequest = OneTimeWorkRequestBuilder<RunningWorker>()
            .setInitialDelay(
                duration,
                TimeUnit.MILLISECONDS
            )
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(App.instance)
            .enqueueUniqueWork(
                alarmEntity.alarmId.toString(),
                ExistingWorkPolicy.REPLACE,
                dailyWorkerRequest
            )

        App.notificationRepository.saveNotificationInDb(alarmEntity)
    }

    private fun getGoalTime(alarmEntity: AlarmEntity) = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, alarmEntity.hour)
        set(Calendar.MINUTE, alarmEntity.minute)
    }

    fun deleteWork(workName: String, context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(workName)
    }
}