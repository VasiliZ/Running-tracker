package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import java.util.*

class Alarm(private val alarmEntity: AlarmEntity) {

    companion object {
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_ALARM_ID = "ALARM_ID"
        private const val RUN_DAILY: Long = 24 * 60 * 60 * 1000
    }

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmEntity.alarmId)
            putExtra(EXTRA_TITLE, alarmEntity.title)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, alarmEntity.alarmId, intent, 0)
        val date = Date(alarmEntity.day)
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, alarmEntity.hour)
            set(Calendar.MINUTE, alarmEntity.minute)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            RUN_DAILY,
            pendingIntent
        )
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmEntity.alarmId, intent, 0)
        alarmManager.cancel(alarmPendingIntent)
        this.alarmEntity.isEnabled = 0
    }
}