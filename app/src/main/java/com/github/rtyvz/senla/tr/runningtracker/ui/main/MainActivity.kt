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
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.HandleClosingActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.NotificationFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.track.CurrentTrackFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.ErrorResponseFirstRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.ErrorResponseNextRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.TracksFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HandleClosingActivityContract, TracksFragment.OnItemClickListListener,
    TracksFragment.LogOutFromApp, ErrorResponseFirstRunDialog.ErrorResponseDialogCallBack,
    ErrorResponseNextRunDialog.ErrorResponseDialogCallBack {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val EMPTY_STRING = ""
        private const val FIRST_TIME_RUN_APP = "FIRST_TIME_RUN_APP"
        private const val EXTRA_LAST_SELECTED_TRACK = "LAST_SELECTED_TRACK"
    }

    private lateinit var userData: UserData
    private var toolBar: Toolbar? = null
    private lateinit var navHeaderUserNameTextView: MaterialTextView
    private lateinit var navHeaderUserEmailTextView: MaterialTextView
    private lateinit var navigationView: NavigationView
    private lateinit var headerNavView: View
    private lateinit var exitFromAppLayout: ConstraintLayout
    private lateinit var drawerLayout: DrawerLayout
    private var trackContainer: FragmentContainerView? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var lastSelectedTrack: TrackEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            lastSelectedTrack = savedInstanceState.getParcelable(EXTRA_LAST_SELECTED_TRACK)
        }

        findViews()
        getUserDataFromPrefs(getSharedPreference())
        setDataToNavHeader()

        drawerToggle?.let {
            drawerLayout.addDrawerListener(it)
        }

        openMainFragment(isFirstTimeLaunchApp(getSharedPreference()))
        setSupportActionBar(toolBar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.main_activity_drawer_open,
            R.string.main_activity_drawer_close
        )

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

        navigationView.setNavigationItemSelectedListener(this)

        exitFromAppLayout.setOnClickListener {
            App.mainRunningRepository.clearCache()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
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

        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
            return
        }

        super.onBackPressed()
    }

    private fun isFirstTimeLaunchApp(sharedPreference: SharedPreferences): Boolean {
        return if (sharedPreference.getBoolean(FIRST_TIME_RUN_APP, true)) {
            sharedPreference.edit().putBoolean(FIRST_TIME_RUN_APP, false).apply()
            true
        } else {
            false
        }
    }

    private fun openMainFragment(isFirstTimeRunFlag: Boolean) {
        showFragment(
            fragment = TracksFragment.newInstance(isFirstTimeRunFlag),
            fragmentTag = TracksFragment.TAG,
            clearToTag = CurrentTrackFragment.TAG,
            clearInclusive = false,
            containerId = R.id.listTrackContainer
        )
        navigationView.setCheckedItem(R.id.mainItem)
    }

    private fun findViews() {
        trackContainer = findViewById(R.id.currentTrackContainer)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        headerNavView = navigationView.getHeaderView(0)
        toolBar = findViewById(R.id.toolBar)
        navHeaderUserEmailTextView = headerNavView.findViewById(R.id.userEmailTextView)
        navHeaderUserNameTextView = headerNavView.findViewById(R.id.userNameTextView)
        exitFromAppLayout = findViewById(R.id.exitFromAppLayout)

    }

    private fun isTrackContainerAvailable() = trackContainer != null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.mainItem -> {
                val fragmentTag = TracksFragment.TAG
                val foundFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                drawerLayout.closeDrawer(GravityCompat.START)
                if (foundFragment != null && fragmentTag == foundFragment.tag) {
                    return true
                } else {
                    showFragment(
                        TracksFragment.newInstance(isFirstTimeLaunchApp(getSharedPreference())),
                        fragmentTag,
                        NotificationFragment.TAG,
                        containerId = R.id.listTrackContainer
                    )
                }
                navigationView.setCheckedItem(item.itemId)
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
                        TracksFragment.TAG,
                        clearInclusive = false,
                        containerId = R.id.listTrackContainer
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
        clearInclusive: Boolean = false,
        containerId: Int
    ) {

        if (clearToTag != null && clearInclusive)
            supportFragmentManager.popBackStack(
                clearToTag,
                if (clearInclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
            )

        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment, fragmentTag)
            .addToBackStack(fragmentTag)
            .commit()
    }

    override fun closeActivity() {
        finish()
    }

    override fun onTrackItemClick(trackEntity: TrackEntity) {
        lastSelectedTrack = trackEntity
        if (isTrackContainerAvailable()) {
            val fragment = supportFragmentManager.findFragmentByTag(CurrentTrackFragment.TAG)
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

    override fun logout() {
        App.mainRunningRepository.clearCache()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun retryRequestTracksData() {
        val fragment = supportFragmentManager.findFragmentByTag(TracksFragment.TAG)
        if (fragment is TracksFragment) {
            fragment.retryRequest()
        }
    }

    override fun retryRequestTracksDataForNextRun() {
        val fragment = supportFragmentManager.findFragmentByTag(TracksFragment.TAG)
        if (fragment is TracksFragment) {
            fragment.getTracksFromDb()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(EXTRA_LAST_SELECTED_TRACK, lastSelectedTrack)

        super.onSaveInstanceState(outState)
    }
}