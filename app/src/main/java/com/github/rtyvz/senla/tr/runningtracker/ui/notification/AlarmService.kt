package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.AlarmBroadcastReceiver.Companion.EXTRA_START_ALARM_SERVICE
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity

class AlarmService : Service() {

    companion object {
        private const val ALARM_SERVICE_CHANEL_ID = "ALARM_SERVICE_CHANEL_ID"
        private const val ALARM_SERVICE_CHANEL_NAME = "ALARM_SERVICE_CHANEL_NAME"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getIntExtra(Alarm.EXTRA_ALARM_ID, 0) ?: 0
        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notifyManager)
        } else {
            ""
        }

        if (intent?.action == EXTRA_START_ALARM_SERVICE) {
            val notificationIntent = Intent(this, RunningActivity::class.java).apply {
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmTitle = intent.getStringExtra("TITLE")
            val notification =
                NotificationCompat.Builder(this, notificationId).setContentTitle(alarmTitle)
                    .setSmallIcon(R.drawable.notifications)
                    .setContentIntent(pendingIntent)
                    .setPriority(
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> NotificationManager.IMPORTANCE_HIGH
                            else -> Notification.PRIORITY_HIGH
                        }
                    )
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build()

            notifyManager.notify(alarmId, notification)
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = ALARM_SERVICE_CHANEL_ID
        val chanelName = ALARM_SERVICE_CHANEL_NAME
        val channel =
            NotificationChannel(channelId, chanelName, NotificationManager.IMPORTANCE_HIGH)
        channel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }
}