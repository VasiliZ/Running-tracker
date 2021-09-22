package com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface MainRunningContract {

    interface ViewMainRunning : MainContract.View {
        fun showFragment(
            fragment: Fragment,
            fragmentTag: String,
            clearToTag: String? = null,
            clearInclusive: Boolean = false,
            containerId: Int
        )
        fun isTrackContainerAvailable(): Boolean
        fun enableHomeButton()
        fun disableHomeButton()
        fun showTrackTextView()
        fun hideTrackTextView()
        fun getFragmentByTag(tag:String): Fragment?
        fun getBackStackEntryCount():Int
        fun popBackStack()
        fun enableToggle()
        fun getRunningPreference(): SharedPreferences
    }

    interface PresenterMainRunning : MainContract.Presenter<ViewMainRunning> {
        fun showCurrentTrackFragment(lastTrack: TrackEntity, containerIsAvalable: Boolean)
        fun showMainFragment()
        fun openMainFragment()
        fun clickTrackItem(trackEntity: TrackEntity)
        fun backPressedClick():Boolean
    }
}