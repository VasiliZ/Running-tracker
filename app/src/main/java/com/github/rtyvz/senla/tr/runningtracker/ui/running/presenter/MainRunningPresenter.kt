package com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter

import android.content.SharedPreferences
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.track.CurrentTrackFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.TracksFragment

class MainRunningPresenter : BasePresenter<MainRunningContract.ViewMainRunning>(),
    MainRunningContract.PresenterMainRunning {

    companion object {
        private const val FIRST_TIME_RUN_APP = "FIRST_TIME_RUN_APP"
    }

    override fun showCurrentTrackFragment(lastTrack: TrackEntity, containerIsAvalable: Boolean) {
        getView().showFragment(
            fragment = CurrentTrackFragment.newInstance(lastTrack),
            fragmentTag = CurrentTrackFragment.TAG,
            containerId = when (getView().isTrackContainerAvailable()) {
                true -> R.id.currentTrackContainer
                else -> R.id.listTrackContainer
            }
        )
    }

    override fun showMainFragment() {
        getView().showFragment(
            fragment = TracksFragment.newInstance(isFirstTimeLaunchApp(getView().getRunningPreference())),
            fragmentTag = TracksFragment.TAG,
            containerId = R.id.listTrackContainer
        )
    }

    override fun openMainFragment() {
        getView().hideTrackTextView()
        showMainFragment()

        if (!getView().isTrackContainerAvailable() && App.state?.lastOpenedUserTrack != null) {
            App.state?.lastOpenedUserTrack?.let {
                showCurrentTrackFragment(it, getView().isTrackContainerAvailable())
            }
            getView().enableHomeButton()
        }

        if (getView().isTrackContainerAvailable() && App.state?.lastOpenedUserTrack == null) {
            getView().showTrackTextView()
        } else {
            App.state?.lastOpenedUserTrack?.let {
                showCurrentTrackFragment(it, getView().isTrackContainerAvailable())
            }
        }
    }

    override fun clickTrackItem(trackEntity: TrackEntity) {
        App.state?.lastOpenedUserTrack = trackEntity

        if (getView().isTrackContainerAvailable()) {
            getView().hideTrackTextView()
            val fragment = getView().getFragmentByTag(CurrentTrackFragment.TAG)

            if (fragment is CurrentTrackFragment) {
                fragment.setTrack(trackEntity)
            } else {
                showCurrentTrackFragment(trackEntity, getView().isTrackContainerAvailable())
            }
        } else {
            getView().enableHomeButton()
            showCurrentTrackFragment(trackEntity, getView().isTrackContainerAvailable())
        }
    }

    override fun backPressedClick(): Boolean {
        return when {
            (getView().getBackStackEntryCount() == 1) -> {
                val fragment = getView().getFragmentByTag(CurrentTrackFragment.TAG)
                if (fragment is CurrentTrackFragment && fragment.isVisible) {
                    getView().popBackStack()

                    App.state?.lastOpenedUserTrack = null
                    getView().disableHomeButton()
                    getView().enableToggle()
                    return false
                }
                return true
            }
            else -> {
                getView().popBackStack()
                getView().disableHomeButton()
                getView().enableToggle()
                getView().showTrackTextView()
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
}