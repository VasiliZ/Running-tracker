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
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.State
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.HandleClosingActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.LogoutFromApp
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.NotificationFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.running.MainRunningFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HandleClosingActivityContract, LogoutFromApp, MainRunningFragment.ChangeNavigationInToolbar {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val EMPTY_STRING = ""
    }

    private var toolBar: Toolbar? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private lateinit var userData: UserData
    private lateinit var navHeaderUserNameTextView: MaterialTextView
    private lateinit var navHeaderUserEmailTextView: MaterialTextView
    private lateinit var navigationView: NavigationView
    private lateinit var headerNavView: View
    private lateinit var exitFromAppLayout: ConstraintLayout
    private lateinit var drawerLayout: DrawerLayout
    private var isToolBarNavigationListenerIsRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val state = App.state
        if (state == null) {
            App.state = State()
        }

        findViews()
        getUserDataFromPrefs(getSharedPreference())
        setDataToNavHeader()

        drawerToggle?.let {
            drawerLayout.addDrawerListener(it)
        }

        setSupportActionBar(toolBar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.main_activity_drawer_open,
            R.string.main_activity_drawer_close
        )

        navigationView.setNavigationItemSelectedListener(this)

        exitFromAppLayout.setOnClickListener {
            logout()
        }

        if (App.state?.isDataLoadedYet == true) {
            openFragmentAfterRotate()
        } else {
            openTracksListFragment()
        }
    }

    private fun openFragmentAfterRotate() {
        val fragment = supportFragmentManager.findFragmentByTag(MainRunningFragment.TAG)
        if (fragment is MainRunningFragment) {
            showFragment(
                MainRunningFragment.newInstance(),
                MainRunningFragment.TAG,
            )
            navigationView.setCheckedItem(R.id.mainItem)
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
            navigationView.setCheckedItem(R.id.notificationsItem)
        }
    }

    private fun openTracksListFragment() {
        showFragment(
            MainRunningFragment.newInstance(),
            MainRunningFragment.TAG,
            MainRunningFragment.TAG,
        )
        navigationView.setCheckedItem(R.id.mainItem)
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
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
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
        headerNavView = navigationView.getHeaderView(0)
        toolBar = findViewById(R.id.toolBar)
        navHeaderUserEmailTextView = headerNavView.findViewById(R.id.userEmailTextView)
        navHeaderUserNameTextView = headerNavView.findViewById(R.id.userNameTextView)
        exitFromAppLayout = findViewById(R.id.exitFromAppLayout)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mainItem -> {
                navigationView.setCheckedItem(item.itemId)
                val fragmentTag = MainRunningFragment.TAG
                val foundFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                drawerLayout.closeDrawer(GravityCompat.START)
                if (foundFragment != null && fragmentTag == foundFragment.tag) {
                    return true
                } else {
                    showFragment(
                        MainRunningFragment.newInstance(),
                        fragmentTag,
                        NotificationFragment.TAG
                    )
                }
                return true
            }
            R.id.notificationsItem -> {
                navigationView.setCheckedItem(item.itemId)
                val fragmentTag = NotificationFragment.TAG
                val foundFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                drawerLayout.closeDrawer(GravityCompat.START)
                if (foundFragment != null && fragmentTag == foundFragment.tag) {
                    return true
                } else {
                    showFragment(
                        NotificationFragment.newInstance(),
                        fragmentTag,
                        MainRunningFragment.TAG
                    )
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
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
        navHeaderUserEmailTextView.text = userData.email
        navHeaderUserNameTextView.text = userData.name
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
                if (isInclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
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
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        supportActionBar?.setDisplayHomeAsUpEnabled(isEnable)
        supportActionBar?.setHomeButtonEnabled(isEnable)

        if (!isToolBarNavigationListenerIsRegistered) {
            drawerToggle?.setToolbarNavigationClickListener {
                onBackPressed()
            }
        }
        isToolBarNavigationListenerIsRegistered = true;
    }


    override fun enableToggle() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        drawerToggle?.isDrawerIndicatorEnabled = true
        drawerToggle?.syncState()
        isToolBarNavigationListenerIsRegistered = false
    }

    override fun logout() {
        App.mainRunningRepository.clearCache()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}