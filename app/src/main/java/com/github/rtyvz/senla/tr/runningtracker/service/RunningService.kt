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
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.SimpleLocation
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.toPointEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity

class RunningService : Service(), LocationListener {

    companion object {
        const val ACTION_RUNNING_SERVICE_STOP = "RUNNING_SERVICE_STOP"
        const val EXTRA_CURRENT_TIME = "CURRENT_TIME"
        const val EXTRA_FINISH_RUNNING_TIME = "FINISH_RUNNING_TIME"
        const val EXTRA_CURRENT_LOCATION = "CURRENT_LOCATION"
        private const val EMPTY_STRING = ""
        private const val EMPTY_LOCATION_PROVIDER = ""
        private const val RUNNING_SERVICE_CHANNEL_ID = "RUNNING_SERVICE_CHANNEL_ID"
        private const val RUNNING_SERVICE_CHANNEL_NAME = "RUNNING_SERVICE_CHANNEL_NAME"
        private const val DEFAULT_LONG_VALUE = 0L
        private const val INITIAL_DISTANCE_BETWEEN_POINTS = 0.0
        private const val MIN_TIME_FOR_LOCATION_UPDATES_MILLIS = 2000L
        private const val MIN_DISTANCE_FOR_LOCATION_UPDATES_METERS = 5F
        private const val OFFSET_FOR_CALCULATE_DISTANCE = 2
        private const val NEXT_INDEX = 1
        private const val UNSENT_TRACK_FLAG = 0
        private const val NOTIFICATION_ID = 1
        private const val INITIAL_TIME = 0L
        private const val INITIAL_DISTANCE = 0
    }

    private val pointsList = mutableListOf<Location>()
    private var startRunningTime = 0L

    override fun onCreate() {
        super.onCreate()

        startForeground()
        startTrackingRunning()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_RUNNING_SERVICE_STOP) {
            val distance = calculateDistance()
            if (pointsList.size > 1) {
                App.mainRunningRepository.saveTrack(
                    TrackEntity(
                        beginsAt = startRunningTime,
                        time = intent.getLongExtra(EXTRA_FINISH_RUNNING_TIME, DEFAULT_LONG_VALUE),
                        distance = distance,
                        isSent = UNSENT_TRACK_FLAG
                    ), pointsList.map {
                        it.toPointEntity(startRunningTime)
                    })
            } else {
                App.mainRunningRepository.removeEmptyTrack(startRunningTime)
                LocalBroadcastManager.getInstance(this)
                    .sendBroadcastSync(Intent(RunningActivity.BROADCAST_ARE_YOU_RUN))
            }

            LocalBroadcastManager.getInstance(this)
                .sendBroadcastSync(Intent(RunningActivity.BROADCAST_RUN_DISTANCE).apply {
                    putExtra(RunningActivity.EXTRA_RUN_DISTANCE, distance)
                })

            stopForeground(true)
            stopSelf()
        } else {
            startRunningTime =
                intent?.getLongExtra(EXTRA_CURRENT_TIME, DEFAULT_LONG_VALUE) ?: DEFAULT_LONG_VALUE
            val startPoint = intent?.getParcelableExtra<SimpleLocation>(EXTRA_CURRENT_LOCATION)

            if (startPoint != null) {
                saveCurrentPoint(Location(EMPTY_LOCATION_PROVIDER).apply {
                    latitude = startPoint.lat
                    longitude = startPoint.lng
                })
            }

            App.mainRunningRepository.insertTracksIntoDB(
                TrackEntity(
                    beginsAt = startRunningTime,
                    time = INITIAL_TIME,
                    distance = INITIAL_DISTANCE,
                    isSent = UNSENT_TRACK_FLAG
                )
            )
        }
        return START_STICKY
    }

    private fun startForeground() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val chanelNotificationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        } else {
            EMPTY_STRING
        }

        val notificationBuilder = NotificationCompat.Builder(this, chanelNotificationId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_running)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(NOTIFICATION_ID, notification)
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
        saveCurrentPoint(location)
    }

    override fun onProviderEnabled(provider: String) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcastSync(Intent(RunningActivity.BROADCAST_GPS_ENABLED))
    }

    override fun onProviderDisabled(provider: String) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcastSync(Intent(RunningActivity.BROADCAST_GPS_DISABLED))
    }

    //todo check this method
    private fun calculateDistance(): Int {
        var distanceBetweenPoints = INITIAL_DISTANCE_BETWEEN_POINTS
        pointsList.forEachIndexed { index, innerLocation ->
            if (index <= pointsList.size - OFFSET_FOR_CALCULATE_DISTANCE) {
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
}