package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.rtyvz.senla.tr.runningtracker.R

class RunningService : Service() {

    companion object {
        const val ACTION_RUNNING_SERVICE_STOP = "RUNNING_SERVICE_STOP"
    }

    override fun onCreate() {
        super.onCreate()

        startForeground()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_RUNNING_SERVICE_STOP) {
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    private fun startForeground() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val chanelNotificationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        } else {
            ""
        }

        val notificationBuilder = NotificationCompat.Builder(this, chanelNotificationId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_running)
            .setPriority(
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        NotificationManager.IMPORTANCE_HIGH
                    }
                    else -> {
                        Notification.PRIORITY_HIGH
                    }
                }
            )
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = "myRunningService"
        val chanelName = "myRunningBackGroundService"
        val channel =
            NotificationChannel(channelId, chanelName, NotificationManager.IMPORTANCE_HIGH)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    override fun onDestroy() {
        Log.d("service stop", "stop")
        super.onDestroy()
    }
}