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
import com.github.rtyvz.senla.tr.runningtracker.ui.LogoutFromApp
import com.github.rtyvz.senla.tr.runningtracker.ui.track.CurrentTrackFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.TracksFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs.ErrorResponseFirstRunDialog

class MainRunningFragment : Fragment(), TracksFragment.OnItemClickListListener,
    TracksFragment.LogOutFromApp, ErrorResponseFirstRunDialog.ErrorResponseDialogCallBack {

    companion object {
        val TAG = MainRunningFragment::class.java.simpleName.toString()
        private const val FIRST_TIME_RUN_APP = "FIRST_TIME_RUN_APP"

        fun newInstance(): MainRunningFragment {
            return MainRunningFragment()
        }
    }

    private var currentTrackContainer: FragmentContainerView? = null

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

        openMainFragment(isFirstTimeLaunchApp(requireActivity().getSharedPreference()))
        App.state?.lastOpenedUserTrack?.let { lastTrack ->
            if (isTrackContainerAvailable()) {
                showFragment(
                    fragment = CurrentTrackFragment.newInstance(lastTrack),
                    fragmentTag = CurrentTrackFragment.TAG,
                    containerId = R.id.currentTrackContainer
                )
            }
        }
    }

    fun onBackPressed(): Boolean {
        return when {
            (childFragmentManager.backStackEntryCount == 1) -> {
                val fragment = childFragmentManager.findFragmentByTag(CurrentTrackFragment.TAG)
                if (fragment is CurrentTrackFragment && fragment.isVisible) {
                    childFragmentManager.popBackStack()
                    App.state?.lastOpenedUserTrack = null
                    return false
                }
                return true
            }
            else -> {
                childFragmentManager.popBackStack()
                App.state?.lastOpenedUserTrack = null
                false
            }
        }
    }

    private fun openMainFragment(isFirstTimeRunFlag: Boolean) {
        showFragment(
            fragment = TracksFragment.newInstance(isFirstTimeRunFlag),
            fragmentTag = TracksFragment.TAG,
            containerId = R.id.listTrackContainer
        )
        if (!isTrackContainerAvailable() && App.state?.lastOpenedUserTrack != null) {
            App.state?.lastOpenedUserTrack?.let {
                showFragment(
                    fragment = CurrentTrackFragment.newInstance(it),
                    fragmentTag = CurrentTrackFragment.TAG,
                    containerId = R.id.listTrackContainer
                )
            }
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
        (activity as LogoutFromApp).logout()
    }

    override fun retryRequestTracksDataFromServer() {
        val fragment = childFragmentManager.findFragmentByTag(TracksFragment.TAG)
        if (fragment is TracksFragment) {
            fragment.retryRequest()
        }
    }

    override fun onTrackItemClick(trackEntity: TrackEntity) {
        App.state?.lastOpenedUserTrack = trackEntity
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
}