package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.content.Intent
import android.view.MenuItem
import androidx.work.WorkManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.NotificationFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.running.MainRunningFragment

class MainActivityPresenter : BasePresenter<MainActivityContract.ViewMainActivity>(),
    MainActivityContract.PresenterMainActivity {

    companion object {
        private const val EMPTY_STRING = ""
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
    }

    private var userData: UserData? = null

    override fun restoreUserData(): UserData? {
        val preferences = getView().getSharedPreference()
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

    override fun initNavHeaderWithData() {
        userData?.let {
            getView().setUserEmailOnNavHeader(it.email)
            getView().setUserNameOnNavHeader(it.name)
        }
    }

    override fun findExistingFragment() {
        when {
            getView().getFragmentByTag(MainRunningFragment.TAG) is MainRunningFragment -> {
                getView().selectItemMenu(R.id.mainItem)
                getView().showFragment(
                    MainRunningFragment.newInstance(),
                    MainRunningFragment.TAG,
                    MainRunningFragment.TAG
                )
            }
            getView().getFragmentByTag(NotificationFragment.TAG) is NotificationFragment -> {
                getView().setTitleActionBar(R.string.main_activity_notifications_toolbar_title)
                getView().selectItemMenu(R.id.notificationsItem)
                getView().showFragment(
                    NotificationFragment.newInstance(),
                    NotificationFragment.TAG,
                    NotificationFragment.TAG,
                    true
                )
            }
            else -> {
                getView().selectItemMenu(R.id.mainItem)
                getView().showFragment(
                    MainRunningFragment.newInstance(),
                    MainRunningFragment.TAG,
                    MainRunningFragment.TAG
                )
            }
        }
    }

    override fun handleBackPress() {
        getView().closeDrawer()
        setInnerFragmentBackPressedBehavior()
    }

    override fun handleNavigationItemSelected(item: MenuItem): Boolean {
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

    override fun logOutFromApp() {
        App.state = null
        App.mainRunningRepository.clearCache()
        getView().getSharedPreference().edit().clear().apply()
        App.mainRunningRepository.cancelAllManagerWork()
        getView().startLoginActivity()
    }

    private fun openRunningFragment(item: MenuItem) {
        val fragmentTag = MainRunningFragment.TAG
        val foundFragment = getView().getFragmentByTag(fragmentTag)

        getView().selectItemMenu(item.itemId)
        getView().setTitleActionBar(R.string.main_activity_running_toolbar_title)
        getView().closeDrawer()

        if (foundFragment != null && fragmentTag == foundFragment.tag) {
            return
        } else {
            getView().showFragment(
                MainRunningFragment.newInstance(),
                fragmentTag,
                NotificationFragment.TAG
            )
        }
    }

    private fun openNotificationFragment(item: MenuItem) {
        val fragmentTag = NotificationFragment.TAG
        val foundFragment = getView().getFragmentByTag(fragmentTag)

        getView().selectItemMenu(item.itemId)
        getView().setTitleActionBar(R.string.main_activity_notifications_toolbar_title)
        getView().closeDrawer()

        if (foundFragment != null && fragmentTag == foundFragment.tag) {
            return
        } else {
            getView().showFragment(
                NotificationFragment.newInstance(),
                fragmentTag,
                MainRunningFragment.TAG
            )
        }
    }

    private fun setInnerFragmentBackPressedBehavior() {
        val fragment = getView().getFragmentByTag(MainRunningFragment.TAG)
        if (fragment is MainRunningFragment && fragment.isVisible) {
            if (fragment.onBackPressed()) {
                App.state = null
                getView().closeActivity()
            }
        } else {
            App.state = null
            getView().closeActivity()
        }
    }
}