package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_START_ALARM_SERVICE = "START_ALARM_SERVICE"
        const val EXTRA_RESCHEDULE_ALARMS = "RESCHEDULE_ALARMS"
        private const val EXTRA_TITLE = "TITLE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            startScheduleAlarmService(context, intent)
        } else {
            startAlarmService(context, intent)
        }
    }

    private fun startScheduleAlarmService(context: Context?, intent: Intent?) {
        val intentService = Intent(context, AlarmService::class.java).apply {
            action = EXTRA_RESCHEDULE_ALARMS
            putExtra(EXTRA_TITLE, intent?.getStringExtra(EXTRA_TITLE))
        }
        context?.startService(intentService)
    }

    private fun startAlarmService(context: Context?, intent: Intent?) {
        val intentService = Intent(context, AlarmService::class.java).apply {
            action = EXTRA_START_ALARM_SERVICE
            putExtra(EXTRA_TITLE, intent?.getStringExtra(EXTRA_TITLE))
        }
        context?.startService(intentService)
    }
}