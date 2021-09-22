package com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter

import android.content.pm.PackageManager
import android.location.LocationManager
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter

class RunningActivityPresenter : BasePresenter<RunningActivityContract.ViewRunningActivity>(),
    RunningActivityContract.PresenterRunningActivity {

    companion object {
        private const val FIRST_ARRAY_INDEX = 0
        const val FINE_LOCATION_REQUEST_CODE = 1101
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