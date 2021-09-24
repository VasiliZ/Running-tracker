package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.view.MenuItem
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.NotificationFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.running.MainRunningFragment

class MainActivityPresenter(private val view: MainActivity) : BasePresenter<BaseView>(view){

    companion object {
        private const val EMPTY_STRING = ""
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
    }

    private var userData: UserData? = null

    fun restoreUserData(): UserData? {
        val preferences = view.getSharedPreference()
        userData = UserData(
            preferences.getString(USER_TOKEN, EMPTY_STRING)
                ?: EMPTY_STRING,
            preferences.getString(USER_NAME, EMPTY_STRING)
                ?: EMPTY_STRING,
            preferences.getString(USER_LAST_NAME, EMPTY_STRING)
                ?: EMPTY_STRING,
            preferences.getString(USER_EMAIL, EMPTY_STRING)
                ?: EMPTY_STRING
        )
        return userData
    }

    fun initNavHeaderWithData() {
        userData?.let {
            view.setUserEmailOnNavHeader(it.email)
            view.setUserNameOnNavHeader(it.name)
        }
    }

    fun findExistingFragment() {
        when {
            view.getFragmentByTag(MainRunningFragment.TAG) is MainRunningFragment -> {
                view.selectItemMenu(R.id.mainItem)
                view.showFragment(
                    MainRunningFragment.newInstance(),
                    MainRunningFragment.TAG,
                    MainRunningFragment.TAG
                )
            }
            view.getFragmentByTag(NotificationFragment.TAG) is NotificationFragment -> {
                view.setTitleActionBar(R.string.main_activity_notifications_toolbar_title)
                view.selectItemMenu(R.id.notificationsItem)
                view.showFragment(
                    NotificationFragment.newInstance(),
                    NotificationFragment.TAG,
                    NotificationFragment.TAG,
                    true
                )
            }
            else -> {
                view.selectItemMenu(R.id.mainItem)
                view.showFragment(
                    MainRunningFragment.newInstance(),
                    MainRunningFragment.TAG,
                    MainRunningFragment.TAG
                )
            }
        }
    }

    fun handleBackPress() {
        view.closeDrawer()
        setInnerFragmentBackPressedBehavior()
    }

    fun handleNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mainItem -> {
                openRunningFragment(item)
                return true
            }
            R.id.notificationsItem -> {
                openNotificationFragment(item)
                return true
            }
            else -> false
        }
    }

    fun logOutFromApp() {
        App.state = null
        App.mainRunningRepository.clearCache()
        view.getSharedPreference().edit().clear().apply()
        App.mainRunningRepository.cancelAllManagerWork()
        view.startLoginActivity()
    }

    private fun openRunningFragment(item: MenuItem) {
        val fragmentTag = MainRunningFragment.TAG
        val foundFragment = view.getFragmentByTag(fragmentTag)

        view.selectItemMenu(item.itemId)
        view.setTitleActionBar(R.string.main_activity_running_toolbar_title)
        view.closeDrawer()

        if (foundFragment != null && fragmentTag == foundFragment.tag) {
            return
        } else {
            view.showFragment(
                MainRunningFragment.newInstance(),
                fragmentTag,
                NotificationFragment.TAG
            )
        }
    }

    private fun openNotificationFragment(item: MenuItem) {
        val fragmentTag = NotificationFragment.TAG
        val foundFragment = view.getFragmentByTag(fragmentTag)

        view.selectItemMenu(item.itemId)
        view.setTitleActionBar(R.string.main_activity_notifications_toolbar_title)
        view.closeDrawer()

        if (foundFragment != null && fragmentTag == foundFragment.tag) {
            return
        } else {
            view.showFragment(
                NotificationFragment.newInstance(),
                fragmentTag,
                MainRunningFragment.TAG
            )
        }
    }

    private fun setInnerFragmentBackPressedBehavior() {
        val fragment = view.getFragmentByTag(MainRunningFragment.TAG)
        if (fragment is MainRunningFragment && fragment.isVisible) {
            if (fragment.onBackPressed()) {
                App.state = null
                view.closeActivity()
            }
        } else {
            App.state = null
            view.closeActivity()
        }
    }
}