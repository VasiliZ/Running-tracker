package com.github.rtyvz.senla.tr.runningtracker.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.toPointEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity
import java.util.*

class RunningService : Service(), LocationListener {

    companion object {
        private const val EMPTY_STRING = ""
        private const val RUNNING_SERVICE_CHANNEL_ID = "RUNNING_SERVICE_CHANNEL_ID"
        private const val RUNNING_SERVICE_CHANNEL_NAME = "RUNNING_SERVICE_CHANNEL_NAME"
        private const val INITIAL_DISTANCE_BETWEEN_POINTS = 0.0
        private const val MIN_TIME_FOR_LOCATION_UPDATES_MILLIS = 2000L
        private const val MIN_DISTANCE_FOR_LOCATION_UPDATES_METERS = 5F
        private const val OFFSET_FOR_CALCULATE_DISTANCE = 1
        private const val NEXT_INDEX = 1
        private const val NOTIFICATION_ID = 1
    }

    private var isServiceStopped: Boolean = false
    private val pointsList = mutableListOf<Location>()
    private var startRunningTime = 0L
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val binder = RunningServiceBinder()

    override fun onCreate() {
        super.onCreate()

        startForeground()
        startTrackingRunning()
    }

    fun getDistance(): Int = calculateDistance()

    fun getTrackPoints(startRunningTime: Long): List<PointEntity> =
        pointsList.map { it.toPointEntity(startRunningTime) }

    private fun startForeground() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val chanelNotificationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        } else {
            EMPTY_STRING
        }

        notificationBuilder = NotificationCompat.Builder(this, chanelNotificationId)
        notificationBuilder.let {
            val notification = it
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_running)
                .setContentText(
                    String.format(
                        Locale.getDefault(),
                        getString(R.string.running_service_distance_pattern),
                        0
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = RUNNING_SERVICE_CHANNEL_ID
        val chanelName = RUNNING_SERVICE_CHANNEL_NAME
        val channel =
            NotificationChannel(channelId, chanelName, NotificationManager.IMPORTANCE_HIGH)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    private fun startTrackingRunning() {
        val permission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_FOR_LOCATION_UPDATES_MILLIS,
                MIN_DISTANCE_FOR_LOCATION_UPDATES_METERS,
                this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        if (!isServiceStopped) {
            saveCurrentPoint(location)
            updateBodyNotification()
        }
    }

    override fun onProviderEnabled(provider: String) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcastSync(Intent(RunningActivity.BROADCAST_GPS_ENABLED))
    }

    override fun onProviderDisabled(provider: String) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcastSync(Intent(RunningActivity.BROADCAST_GPS_DISABLED))
    }

    private fun calculateDistance(): Int {
        var distanceBetweenPoints = INITIAL_DISTANCE_BETWEEN_POINTS
        pointsList.forEachIndexed { index, innerLocation ->
            if (index < pointsList.size - OFFSET_FOR_CALCULATE_DISTANCE) {
                distanceBetweenPoints += innerLocation.distanceTo(pointsList[index + NEXT_INDEX])
            }
        }
        return distanceBetweenPoints.toInt()
    }

    private fun saveCurrentPoint(location: Location) {
        pointsList.add(location)
        App.mainRunningRepository.insertLocationIntoDb(
            location.toPointEntity(
                startRunningTime
            )
        )
    }

    private fun updateBodyNotification() {
        NotificationManagerCompat.from(this).notify(
            NOTIFICATION_ID,
            notificationBuilder
                .setSilent(true)
                .setContentText(
                    String.format(
                        Locale.getDefault(),
                        getString(R.string.running_service_distance_pattern),
                        calculateDistance()
                    )
                ).build()
        )
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager).removeUpdates(this)

        super.onDestroy()
    }

    inner class RunningServiceBinder : Binder() {
        fun getService(): RunningService = this@RunningService
    }
}