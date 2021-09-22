package com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter

import android.content.pm.PackageManager
import android.location.LocationManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter

class RunningActivityPresenter : BasePresenter<RunningActivityContract.ViewRunningActivity>(),
    RunningActivityContract.PresenterRunningActivity {

    companion object {
        private const val FIRST_ARRAY_INDEX = 0
        const val FINE_LOCATION_REQUEST_CODE = 1101
        private const val INITIAL_TIME = 0L
        private const val INITIAL_DISTANCE = 0
        private const val UNSENT_TRACK_FLAG = 0
        private const val EMPTY_DISTANCE = 0
        private const val NOT_EMPTY_LIST_SIZE = 1
    }

    override fun startRunning() {
        if (isGpsEnabled()) {
            getView().initTimer()
            getView().startAnimation()
            getView().setUpAnimatedLayouts()
            getView().startTimer()
            getView().getDeviceLocation()
            getView().updateLocationUi()
            getView().startRunningService()
        } else {
            getView().showEnableGpsDialog()
        }
    }

    override fun checkFinishButtonWasClicked(
        itemId: Int,
        startButtonFlag: Boolean,
        finishButtonFlag: Boolean
    ): Boolean {
        return when (itemId) {
            android.R.id.home -> {
                if (startButtonFlag && !finishButtonFlag) {
                    getView().showNeedsClickFinishToast()
                    false
                } else {
                    getView().finishActivity()
                    true
                }
            }
            else -> {
                false
            }
        }
    }

    override fun checkRequstPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FINE_LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[FIRST_ARRAY_INDEX] != PackageManager.PERMISSION_GRANTED) {
                    getView().finishActivity()
                } else {
                    getView().setGrandPermissionFlag(true)
                }
            }
        }

        getView().getDeviceLocation()
        getView().updateLocationUi()
    }

    override fun stopRunning() {
        getView().changeButtonClickable()
        getView().stopRunAnimation()
        getView().disableButtons()
        getView().stopRunningService()
        getView().stopTimer()
        getView().displayRunningTime()
    }

    override fun saveTrack(startRunningTime: Long) {
        App.mainRunningRepository.insertTracksIntoDB(
            TrackEntity(
                beginsAt = startRunningTime,
                time = INITIAL_TIME,
                distance = INITIAL_DISTANCE,
                isSent = UNSENT_TRACK_FLAG
            )
        )
    }

    override fun updateTrackAfterRun(
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
            getView().showAreYouRunningDialog()
        }
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = getView().getLocationManager()
        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            true
        } else {
            getView().showEnableGpsDialog()
            false
        }
    }
}