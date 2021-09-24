package com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter

import android.content.SharedPreferences
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.running.MainRunningFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.track.CurrentTrackFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.TracksFragment

class MainRunningPresenter(private val view: MainRunningFragment) : BasePresenter<BaseView>(view) {

    companion object {
        private const val FIRST_TIME_RUN_APP = "FIRST_TIME_RUN_APP"
    }

    private fun showCurrentTrackFragment(lastTrack: TrackEntity) {
        view.showFragment(
            fragment = CurrentTrackFragment.newInstance(lastTrack),
            fragmentTag = CurrentTrackFragment.TAG,
            containerId = when (view.isTrackContainerAvailable()) {
                true -> R.id.currentTrackContainer
                else -> R.id.listTrackContainer
            }
        )
    }

    private fun showMainFragment() {
        view.showFragment(
            fragment = TracksFragment.newInstance(isFirstTimeLaunchApp(view.getRunningPreference())),
            fragmentTag = TracksFragment.TAG,
            containerId = R.id.listTrackContainer
        )
    }

    private fun openMainFragment() {
        view.hideTrackTextView()
        showMainFragment()

        if (!view.isTrackContainerAvailable() && App.state?.lastOpenedUserTrack != null) {
            App.state?.lastOpenedUserTrack?.let {
                showCurrentTrackFragment(it)
            }
            view.enableHomeButton()
        }

        if (view.isTrackContainerAvailable() && App.state?.lastOpenedUserTrack == null) {
            view.showTrackTextView()
        } else {
            App.state?.lastOpenedUserTrack?.let {
                showCurrentTrackFragment(it)
            }
        }
    }

    fun onTrackItemClick(trackEntity: TrackEntity) {
        App.state?.lastOpenedUserTrack = trackEntity

        if (view.isTrackContainerAvailable()) {
            view.hideTrackTextView()
            val fragment = view.getFragmentByTag(CurrentTrackFragment.TAG)

            if (fragment is CurrentTrackFragment) {
                fragment.setTrack(trackEntity)
            } else {
                showCurrentTrackFragment(trackEntity)
            }
        } else {
            view.enableHomeButton()
            showCurrentTrackFragment(trackEntity)
        }
    }

    fun onBackPressedClick(): Boolean {
        return when {
            (view.getBackStackEntryCount() == 1) -> {
                val fragment = view.getFragmentByTag(CurrentTrackFragment.TAG)
                if (fragment is CurrentTrackFragment && fragment.isVisible) {
                    view.popBackStack()

                    App.state?.lastOpenedUserTrack = null
                    view.disableHomeButton()
                    view.enableToggle()
                    return false
                }
                return true
            }
            else -> {
                view.popBackStack()
                view.disableHomeButton()
                view.enableToggle()
                view.showTrackTextView()
                App.state?.lastOpenedUserTrack = null
                false
            }
        }
    }

    private fun isFirstTimeLaunchApp(sharedPreference: SharedPreferences): Boolean {
        return if (sharedPreference.getBoolean(FIRST_TIME_RUN_APP, true)) {
            sharedPreference.edit().putBoolean(FIRST_TIME_RUN_APP, false)
                .apply()
            true
        } else {
            false
        }
    }

    fun onCreate() {
        openMainFragment()
    }
}