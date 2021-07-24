package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R

class RunningService : Service(), LocationListener {

    companion object {
        const val ACTION_RUNNING_SERVICE_STOP = "RUNNING_SERVICE_STOP"
    }

    private val pointsList = mutableListOf<Location>()
    private var beginRunningAt: Long = 0L

    override fun onCreate() {
        super.onCreate()

        startForeground()
        startRunningTracking()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_RUNNING_SERVICE_STOP) {
            LocalBroadcastManager.getInstance(this)
                .sendBroadcast(Intent(RunningActivity.BROADCAST_RUN_DISTANCE).apply {
                    putExtra(RunningActivity.EXTRA_RUN_DISTANCE, calculateDistance())
                })
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
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> NotificationManager.IMPORTANCE_HIGH
                    else -> Notification.PRIORITY_HIGH
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


    private fun startRunningTracking() {
        val permission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            beginRunningAt = System.currentTimeMillis()

            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                5F,
                this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        pointsList.add(location)
        App.mainRunningRepository.insertLocationIntoDb(location, beginRunningAt)
    }

    private fun calculateDistance(): Double {
        var distanceBetweenPoints = 0.0
        pointsList.forEachIndexed { innerIndex, innerLocation ->
            if (innerIndex <= pointsList.size - 2) {
                distanceBetweenPoints += innerLocation.distanceTo(pointsList[innerIndex + 1])
            }
        }
        return distanceBetweenPoints
    }

    override fun onDestroy() {
        Log.d("distance", "distanceBetweenPoints".toString())
        super.onDestroy()
    }
}