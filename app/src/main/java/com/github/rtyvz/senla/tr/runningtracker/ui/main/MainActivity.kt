package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.work.WorkManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.State
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.ClosableActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.LogoutFromApp
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.NotificationFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ClosableActivity, LogoutFromApp, MainRunningFragment.ChangeNavigationInToolbar {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val EMPTY_STRING = ""
        private const val FIRST_NAVIGATION_HEADER_ITEM = 0
        private const val DIFFERENT_FLAG_FOR_BACK_STACK = 0
    }

    private var toolBar: Toolbar? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var navHeaderUserNameTextView: MaterialTextView? = null
    private var navHeaderUserEmailTextView: MaterialTextView? = null
    private var navigationView: NavigationView? = null
    private var headerNavView: View? = null
    private var exitFromAppLayout: ConstraintLayout? = null
    private var drawerLayout: DrawerLayout? = null
    private lateinit var userData: UserData
    private var isToolBarNavigationListenerIsRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val state = App.state
        if (state == null) {
            App.state = State()
        }

        findViews()
        getUserDataFromPrefs(getRunningSharedPreference())
        setDataToNavHeader()

        drawerToggle?.let {
            drawerLayout?.addDrawerListener(it)
        }

        setSupportActionBar(toolBar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.main_activity_drawer_open,
            R.string.main_activity_drawer_close
        )

        navigationView?.setNavigationItemSelectedListener(this)

        exitFromAppLayout?.setOnClickListener {
            logout()
        }

        if (App.state?.isDataLoadedYet == true) {
            findExistingFragment()
        } else {
            openTracksListFragment()
        }
    }

    private fun findExistingFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(MainRunningFragment.TAG)

        if (fragment is MainRunningFragment) {
            showFragment(
                MainRunningFragment.newInstance(),
                MainRunningFragment.TAG,
            )
            navigationView?.setCheckedItem(R.id.mainItem)
        }
        val fragmentNotification =
            supportFragmentManager.findFragmentByTag(NotificationFragment.TAG)

        if (fragmentNotification is NotificationFragment) {
            showFragment(
                NotificationFragment.newInstance(),
                NotificationFragment.TAG,
                NotificationFragment.TAG,
                false
            )
            navigationView?.setCheckedItem(R.id.notificationsItem)
        }
    }

    private fun openTracksListFragment() {
        showFragment(
            MainRunningFragment.newInstance(),
            MainRunningFragment.TAG,
            MainRunningFragment.TAG,
        )
        navigationView?.setCheckedItem(R.id.mainItem)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle?.onOptionsItemSelected(item) == true) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerVisible(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
            return
        }
        setInnerFragmentBackPressedBehavior()
    }

    private fun setInnerFragmentBackPressedBehavior() {
        val fragment = supportFragmentManager.findFragmentByTag(MainRunningFragment.TAG)
        if (fragment is MainRunningFragment && fragment.isVisible) {
            if (fragment.onBackPressed()) {
                App.state = null
                finish()
            }
        } else {
            App.state = null
            finish()
        }
    }

    private fun findViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        headerNavView = navigationView?.getHeaderView(FIRST_NAVIGATION_HEADER_ITEM)
        toolBar = findViewById(R.id.toolBar)
        navHeaderUserEmailTextView = headerNavView?.findViewById(R.id.userEmailTextView)
        navHeaderUserNameTextView = headerNavView?.findViewById(R.id.userNameTextView)
        exitFromAppLayout = findViewById(R.id.exitFromAppLayout)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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

    private fun openRunningFragment(item: MenuItem) {
        navigationView?.setCheckedItem(item.itemId)
        val fragmentTag = MainRunningFragment.TAG
        val foundFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        drawerLayout?.closeDrawer(GravityCompat.START)
        if (foundFragment != null && fragmentTag == foundFragment.tag) {
            return
        } else {
            showFragment(
                MainRunningFragment.newInstance(),
                fragmentTag,
                NotificationFragment.TAG
            )
        }
    }

    private fun openNotificationFragment(item: MenuItem) {
        navigationView?.setCheckedItem(item.itemId)
        val fragmentTag = NotificationFragment.TAG
        val foundFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        drawerLayout?.closeDrawer(GravityCompat.START)
        if (foundFragment != null && fragmentTag == foundFragment.tag) {
            return
        } else {
            showFragment(
                NotificationFragment.newInstance(),
                fragmentTag,
                MainRunningFragment.TAG
            )
        }
    }

    private fun getUserDataFromPrefs(preferences: SharedPreferences) {
        userData = UserData(
            preferences.getString(USER_TOKEN, EMPTY_STRING) ?: EMPTY_STRING,
            preferences.getString(USER_NAME, EMPTY_STRING) ?: EMPTY_STRING,
            preferences.getString(USER_LAST_NAME, EMPTY_STRING) ?: EMPTY_STRING,
            preferences.getString(USER_EMAIL, EMPTY_STRING) ?: EMPTY_STRING
        )
    }

    private fun setDataToNavHeader() {
        navHeaderUserEmailTextView?.text = userData.email
        navHeaderUserNameTextView?.text = userData.name
    }

    private fun showFragment(
        fragment: Fragment,
        fragmentTag: String,
        clearToTag: String? = null,
        isInclusive: Boolean = true
    ) {
        if (clearToTag != null)
            supportFragmentManager.popBackStack(
                clearToTag,
                if (isInclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else DIFFERENT_FLAG_FOR_BACK_STACK
            )

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, fragment, fragmentTag)
            .addToBackStack(fragmentTag)
            .commit()
    }

    override fun closeActivity() {
        finish()
    }

    override fun enableHomeButton(isEnable: Boolean) {
        drawerToggle?.isDrawerIndicatorEnabled = false
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        supportActionBar?.setDisplayHomeAsUpEnabled(isEnable)
        supportActionBar?.setHomeButtonEnabled(isEnable)

        if (!isToolBarNavigationListenerIsRegistered) {
            drawerToggle?.setToolbarNavigationClickListener {
                onBackPressed()
            }
        }
        isToolBarNavigationListenerIsRegistered = true
    }


    override fun enableToggle() {
        isToolBarNavigationListenerIsRegistered = false
        drawerToggle?.isDrawerIndicatorEnabled = true
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        drawerToggle?.syncState()
    }

    override fun logout() {
        App.state = null
        App.mainRunningRepository.clearCache()
        getRunningSharedPreference().edit().clear().apply()
        WorkManager.getInstance(this).cancelAllWork()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        drawerToggle = null

        super.onDestroy()
    }
}