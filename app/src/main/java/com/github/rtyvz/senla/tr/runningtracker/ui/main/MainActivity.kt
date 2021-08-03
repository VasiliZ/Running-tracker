package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.HandleClosingActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
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
    }

    private lateinit var userData: UserData
    private lateinit var toolBar: Toolbar
    private lateinit var navHeaderUserNameTextView: MaterialTextView
    private lateinit var navHeaderUserEmailTextView: MaterialTextView
    private lateinit var navigationView: NavigationView
    private lateinit var headerNavView: View
    private lateinit var exitFromAppLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        getUserDataFromPrefs(getSharedPreference())
        setDataToNavHeader()
        openMainFragment(isFirstTimeLaunchApp(getSharedPreference()))
        setSupportActionBar(toolBar)
        navigationView.setNavigationItemSelectedListener(this)

        exitFromAppLayout.setOnClickListener {
            App.mainRunningRepository.clearCache()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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

    private fun openMainFragment(isFirstTimeRunFlag: Boolean) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                TracksFragment.newInstance(isFirstTimeRunFlag),
                TracksFragment.TAG
            )
            .addToBackStack(TracksFragment.TAG)
            .commit()
        navigationView.setCheckedItem(R.id.mainItem)
    }

    private fun findViews() {
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
                return true
            }
            R.id.notificationsItem -> {
                navigationView.setCheckedItem(item.itemId)
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

    override fun closeActivity() {
        finish()
    }

    override fun onItemClick(trackEntity: TrackEntity) {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainer,
            CurrentTrackFragment.newInstance(trackEntity),
            CurrentTrackFragment.TAG
        ).addToBackStack(CurrentTrackFragment.TAG)
            .commit()
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
}