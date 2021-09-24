package com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter

import android.content.pm.PackageManager
import android.location.LocationManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity

class RunningActivityPresenter(private val view: RunningActivity) :
    BasePresenter<BaseView>(view) {

    companion object {
        private const val FIRST_ARRAY_INDEX = 0
        const val FINE_LOCATION_REQUEST_CODE = 1101
        private const val INITIAL_TIME = 0L
        private const val INITIAL_DISTANCE = 0
        private const val UNSENT_TRACK_FLAG = 0
        private const val EMPTY_DISTANCE = 0
        private const val NOT_EMPTY_LIST_SIZE = 1
    }

    fun startRunning() {
        if (isGpsEnabled()) {
            view.initTimer()
            view.startAnimation()
            view.setUpAnimatedLayouts()
            view.startTimer()
            view.getDeviceLocation()
            view.updateLocationUi()
            view.startRunningService()
        } else {
            view.showEnableGpsDialog()
        }
    }

    fun checkFinishButtonWasClicked(
        itemId: Int,
        startButtonFlag: Boolean,
        finishButtonFlag: Boolean
    ): Boolean {
        return when (itemId) {
            android.R.id.home -> {
                if (startButtonFlag && !finishButtonFlag) {
                    view.showNeedsClickFinishToast()
                    false
                } else {
                    view.finishActivity()
                    true
                }
            }
            else -> {
                false
            }
        }
    }

    fun checkRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FINE_LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[FIRST_ARRAY_INDEX] != PackageManager.PERMISSION_GRANTED) {
                    view.finishActivity()
                } else {
                    view.setGrandPermissionFlag(true)
                }
            }
        }

        view.getDeviceLocation()
        view.updateLocationUi()
    }

    fun stopRunning() {
        view.changeButtonClickable()
        view.stopRunAnimation()
        view.disableButtons()
        view.stopRunningService()
        view.stopTimer()
        view.displayRunningTime()
    }

    fun saveTrack(startRunningTime: Long) {
        App.mainRunningRepository.insertTracksIntoDB(
            TrackEntity(
                beginsAt = startRunningTime,
                time = INITIAL_TIME,
                distance = INITIAL_DISTANCE,
                isSent = UNSENT_TRACK_FLAG
            )
        )
    }

    fun updateTrackAfterRun(
        pointsList: List<PointEntity>?,
        distance: Int?,
        startRunningTime: Long,
        time: Long
    ) {
        val points = pointsList ?: emptyList()
        val countMeters = distance ?: 0
        if (points.size > NOT_EMPTY_LIST_SIZE && countMeters >= EMPTY_DISTANCE) {
            App.mainRunningRepository.saveTrack(
                TrackEntity(
                    beginsAt = startRunningTime,
                    time = time,
                    distance = countMeters,
                    isSent = UNSENT_TRACK_FLAG
                ), points
            )
        } else {
            App.mainRunningRepository.removeEmptyTrack(startRunningTime)
            App.mainRunningRepository.removeTrackPoints(startRunningTime)
            view.showAreYouRunningDialog()
        }
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = view.getLocationManager()
        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            true
        } else {
            view.showEnableGpsDialog()
            false
        }
    }
}