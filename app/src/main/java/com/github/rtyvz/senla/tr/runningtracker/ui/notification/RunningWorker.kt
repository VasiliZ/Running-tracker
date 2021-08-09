package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.NotificationWorkManager.Companion.EXTRA_ALARM_ID
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity

class RunningWorker(private val context: Context, private val params: WorkerParameters) :
    Worker(context, params) {

    companion object {
        private const val ALARM_SERVICE_CHANEL_ID = "ALARM_SERVICE_CHANEL_ID"
        private const val ALARM_SERVICE_CHANEL_NAME = "ALARM_SERVICE_CHANEL_NAME"
    }

    override fun doWork(): Result {
        val alarmId = inputData.getInt(EXTRA_ALARM_ID, 0)
        val notifyManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notifyManager)
        } else {
            ""
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, RunningActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification =
            NotificationCompat.Builder(context, notificationId)
                .setContentTitle(inputData.getString("TITLE"))
                .setSmallIcon(R.drawable.notifications)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build()

        NotificationManagerCompat.from(context).notify(alarmId, notification)
        return Result.success()
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