package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorkManager {

    companion object {
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_ALARM_ID = "ALARM_ID"
    }

    fun createWorkForNotification(alarmEntity: AlarmEntity) {
        val data = Data.Builder().apply {
            putString(EXTRA_TITLE, alarmEntity.title)
            putInt(EXTRA_ALARM_ID, alarmEntity.alarmId)
        }
        val duration = getGoalTime(alarmEntity).timeInMillis - Calendar.getInstance().timeInMillis
        val workerRequest = OneTimeWorkRequest.Builder(NotificationRunningWorker::class.java)
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(App.instance)
            .enqueueUniqueWork(
                alarmEntity.alarmId.toString(),
                ExistingWorkPolicy.REPLACE,
                workerRequest
            )
    }

    private fun getGoalTime(alarmEntity: AlarmEntity) = Calendar.getInstance().apply {
        time = Date(alarmEntity.day)
        set(Calendar.HOUR_OF_DAY, alarmEntity.hour)
        set(Calendar.MINUTE, alarmEntity.minute)
    }

    fun deleteWork(workName: String) {
        WorkManager.getInstance(App.instance).cancelAllWorkByTag(workName)
    }
}