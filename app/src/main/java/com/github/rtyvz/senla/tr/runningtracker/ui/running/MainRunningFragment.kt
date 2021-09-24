package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.LogoutFromApp
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter.MainRunningPresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.TracksFragment
import com.google.android.material.textview.MaterialTextView

class MainRunningFragment :
    BaseFragment<MainRunningPresenter>(),
    BaseView, TracksFragment.OnItemClickListListener,
    TracksFragment.LogOutFromApp {

    companion object {
        val TAG = MainRunningFragment::class.java.simpleName.toString()

        fun newInstance(): MainRunningFragment {
            return MainRunningFragment()
        }
    }

    private var currentTrackContainer: FragmentContainerView? = null
    private var selectTrackTextView: MaterialTextView? = null

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
        selectTrackTextView = view.findViewById(R.id.selectTrackTextView)
        presenter.onCreate()
    }

    fun onBackPressed(): Boolean {
        return presenter.onBackPressedClick()
    }

    fun isTrackContainerAvailable() = currentTrackContainer != null

    fun enableHomeButton() {
        (activity as ChangeNavigationInToolbar).enableHomeButton(true)
    }

    fun disableHomeButton() {
        (activity as ChangeNavigationInToolbar).enableHomeButton(false)
    }

    fun showTrackTextView() {
        selectTrackTextView?.isVisible = true
    }

    fun hideTrackTextView() {
        selectTrackTextView?.isVisible = false
    }

    fun getFragmentByTag(tag: String): Fragment? {
        return childFragmentManager.findFragmentByTag(tag)
    }

    fun getBackStackEntryCount(): Int {
        return childFragmentManager.backStackEntryCount
    }

    fun popBackStack() {
        childFragmentManager.popBackStack()
    }

    fun enableToggle() {
        (activity as ChangeNavigationInToolbar).enableToggle()
    }

    fun getRunningPreference(): SharedPreferences {
        return requireActivity().getRunningSharedPreference()
    }

    override fun logout() {
        App.mainRunningRepository.clearCache()
        (activity as LogoutFromApp).logout()
    }

    override fun onTrackItemClick(trackEntity: TrackEntity) {
        presenter.onTrackItemClick(trackEntity)
    }

    fun showFragment(
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

    override fun onDestroyView() {
        currentTrackContainer = null
        selectTrackTextView = null

        super.onDestroyView()
    }

    interface ChangeNavigationInToolbar {
        fun enableHomeButton(isEnable: Boolean)
        fun enableToggle()
    }

    override fun createPresenter() = MainRunningPresenter(this)
}