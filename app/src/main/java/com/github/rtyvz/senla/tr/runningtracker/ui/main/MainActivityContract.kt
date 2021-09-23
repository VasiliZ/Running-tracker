package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.content.SharedPreferences
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface MainActivityContract {

    interface ViewMainActivity : MainContract.View {
        fun getSharedPreference(): SharedPreferences
        fun setUserNameOnNavHeader(name: String)
        fun setUserEmailOnNavHeader(email: String)
        fun setTitleActionBar(resId: Int)
        fun showFragment(
            fragment: Fragment,
            fragmentTag: String,
            clearToTag: String? = null,
            isInclusive: Boolean = true
        )

        fun getFragmentByTag(tag: String): Fragment?
        fun selectItemMenu(itemId: Int)
        fun closeDrawer()
        fun closeActivity()
        fun startLoginActivity()
    }

    interface PresenterMainActivity : MainContract.Presenter<ViewMainActivity> {
        fun restoreUserData(): UserData?
        fun initNavHeaderWithData()
        fun findExistingFragment()
        fun handleBackPress()
        fun handleNavigationItemSelected(item: MenuItem): Boolean
        fun logOutFromApp()
    }
}