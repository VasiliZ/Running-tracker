package com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter

import android.location.LocationManager
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView

interface ViewRunningActivity : BaseView {
    fun initTimer()
    fun startAnimation()
    fun setUpAnimatedLayouts()
    fun startTimer()
    fun getDeviceLocation()
    fun updateLocationUi()
    fun startRunningService()
    fun showEnableGpsDialog()
    fun getLocationManager(): LocationManager
    fun finishActivity()
    fun showNeedsClickFinishToast()
    fun setGrandPermissionFlag(flag: Boolean)
    fun stopRunAnimation()
    fun disableButtons()
    fun changeButtonClickable()
    fun stopRunningService()
    fun stopTimer()
    fun displayRunningTime()
    fun showAreYouRunningDialog()
}