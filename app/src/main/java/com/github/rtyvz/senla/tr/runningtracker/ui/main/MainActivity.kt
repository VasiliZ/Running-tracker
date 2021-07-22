package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.HandleClosingActivityContract
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HandleClosingActivityContract {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val EMPTY_STRING = ""
    }

    private lateinit var userData: UserData
    private lateinit var toolBar: Toolbar
    private lateinit var navHeaderUserNameTextView: MaterialTextView
    private lateinit var navHeaderUserEmailTextView: MaterialTextView
    private lateinit var navigationView: NavigationView
    private lateinit var headerNavView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        getUserDataFromPrefs()
        setSupportActionBar(toolBar)
        setDataToNavHeader()
        openMainFragment()
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun openMainFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, MainFragment.newInstance())
            .addToBackStack(MainFragment.TAG)
            .commit()
        navigationView.setCheckedItem(R.id.mainItem)
    }

    private fun findViews() {
        toolBar = findViewById(R.id.toolBar)
        navigationView = findViewById(R.id.navigationView)
        headerNavView = navigationView.getHeaderView(0)
        navHeaderUserEmailTextView = headerNavView.findViewById(R.id.userEmailTextView)
        navHeaderUserNameTextView = headerNavView.findViewById(R.id.userNameTextView)

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

    private fun getUserDataFromPrefs() {
        val prefs = this.getSharedPreference()
        userData = UserData(
            prefs.getString(USER_TOKEN, EMPTY_STRING) ?: EMPTY_STRING,
            prefs.getString(USER_NAME, EMPTY_STRING) ?: EMPTY_STRING,
            prefs.getString(USER_LAST_NAME, EMPTY_STRING) ?: EMPTY_STRING,
            prefs.getString(USER_EMAIL, EMPTY_STRING) ?: EMPTY_STRING
        )
    }

    private fun setDataToNavHeader() {
        navHeaderUserEmailTextView.text = userData.email
        navHeaderUserNameTextView.text = userData.name
    }

    override fun finishActivity() {
        finish()
    }
}