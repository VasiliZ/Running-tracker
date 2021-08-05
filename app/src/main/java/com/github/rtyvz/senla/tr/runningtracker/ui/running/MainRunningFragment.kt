package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.track.CurrentTrackFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.ErrorResponseFirstRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.ErrorResponseNextRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.TracksFragment

class MainRunningFragment : Fragment(), TracksFragment.OnItemClickListListener,
    TracksFragment.LogOutFromApp, ErrorResponseFirstRunDialog.ErrorResponseDialogCallBack,
    ErrorResponseNextRunDialog.ErrorResponseDialogCallBack {

    companion object {
        val TAG = MainRunningFragment::class.java.simpleName.toString()
        private const val FIRST_TIME_RUN_APP = "FIRST_TIME_RUN_APP"
        private const val EXTRA_LAST_SELECTED_TRACK = "LAST_SELECTED_TRACK"

        fun newInstance(): MainRunningFragment {
            return MainRunningFragment()
        }
    }

    private var currentTrackContainer: FragmentContainerView? = null

    private var lastSelectedTrack: TrackEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrackContainer = view.findViewById(R.id.currentTrackContainer)

        if (savedInstanceState != null) {
            lastSelectedTrack =
                savedInstanceState.getParcelable(EXTRA_LAST_SELECTED_TRACK)
        }

        openMainFragment(isFirstTimeLaunchApp(requireActivity().getSharedPreference()))
        lastSelectedTrack?.let { lastTrack ->
            if (isTrackContainerAvailable()) {
                showFragment(
                    CurrentTrackFragment.newInstance(lastTrack),
                    CurrentTrackFragment.TAG,
                    clearInclusive = false,
                    containerId = R.id.currentTrackContainer
                )
            }
        }
    }

    private fun openMainFragment(isFirstTimeRunFlag: Boolean) {
        val fragment = childFragmentManager.findFragmentByTag(CurrentTrackFragment.TAG)
        if (fragment is CurrentTrackFragment && !isTrackContainerAvailable()) {
            lastSelectedTrack?.let {
                showFragment(
                    fragment = CurrentTrackFragment.newInstance(it),
                    fragmentTag = CurrentTrackFragment.TAG,
                    clearToTag = CurrentTrackFragment.TAG,
                    clearInclusive = true,
                    containerId = R.id.currentTrackContainer
                )
            }
        } else {
            showFragment(
                fragment = TracksFragment.newInstance(isFirstTimeRunFlag),
                fragmentTag = TracksFragment.TAG,
                clearToTag = TracksFragment.TAG,
                clearInclusive = true,
                containerId = R.id.listTrackContainer
            )
        }
    }

    private fun isFirstTimeLaunchApp(sharedPreference: SharedPreferences): Boolean {
        return if (sharedPreference.getBoolean(FIRST_TIME_RUN_APP, true)) {
            sharedPreference.edit().putBoolean(FIRST_TIME_RUN_APP, false).apply()
            true
        } else {
            false
        }
    }


    private fun isTrackContainerAvailable() = currentTrackContainer != null

    override fun logout() {
        App.mainRunningRepository.clearCache()
    }

    override fun retryRequestTracksDataFromServer() {
        val fragment = childFragmentManager.findFragmentByTag(TracksFragment.TAG)
        if (fragment is TracksFragment) {
            fragment.retryRequest()
        }
    }

    override fun retryRequestTracksDataFromDb() {
        val fragment = childFragmentManager.findFragmentByTag(TracksFragment.TAG)
        if (fragment is TracksFragment) {
            fragment.getTracksFromDb()
        }
    }

    override fun onTrackItemClick(trackEntity: TrackEntity) {
        lastSelectedTrack = trackEntity
        if (isTrackContainerAvailable()) {
            val fragment = childFragmentManager.findFragmentByTag(CurrentTrackFragment.TAG)
            if (fragment is CurrentTrackFragment) {
                fragment.setTrack(trackEntity)
            } else {
                showFragment(
                    CurrentTrackFragment.newInstance(trackEntity),
                    CurrentTrackFragment.TAG,
                    containerId = R.id.currentTrackContainer
                )
            }
        } else {
            showFragment(
                CurrentTrackFragment.newInstance(trackEntity),
                CurrentTrackFragment.TAG,
                containerId = R.id.listTrackContainer
            )
        }
    }

    private fun showFragment(
        fragment: Fragment,
        fragmentTag: String,
        clearToTag: String? = null,
        clearInclusive: Boolean = false,
        containerId: Int
    ) {

        if (clearToTag != null)
            childFragmentManager.popBackStack(
                clearToTag,
                if (clearInclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
            )

        childFragmentManager.beginTransaction()
            .replace(containerId, fragment, fragmentTag)
            .addToBackStack(fragmentTag)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(EXTRA_LAST_SELECTED_TRACK, lastSelectedTrack)

        super.onSaveInstanceState(outState)
    }
}