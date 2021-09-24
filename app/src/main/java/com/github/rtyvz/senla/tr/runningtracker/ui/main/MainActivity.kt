package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.State
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.LogoutFromApp
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.MainRunningFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView

class MainActivity :
    BaseActivity<MainActivityPresenter>(),
    BaseView, NavigationView.OnNavigationItemSelectedListener,
    LogoutFromApp, MainRunningFragment.ChangeNavigationInToolbar {

    companion object {
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
    private var isToolBarNavigationListenerIsRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val state = App.state
        if (state == null) {
            App.state = State()
        }

        initViews()
        presenter.restoreUserData()
        presenter.initNavHeaderWithData()

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
        presenter.findExistingFragment()
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
        presenter.handleBackPress()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        headerNavView = navigationView?.getHeaderView(FIRST_NAVIGATION_HEADER_ITEM)
        toolBar = findViewById(R.id.toolBar)
        navHeaderUserEmailTextView = headerNavView?.findViewById(R.id.userEmailTextView)
        navHeaderUserNameTextView = headerNavView?.findViewById(R.id.userNameTextView)
        exitFromAppLayout = findViewById(R.id.exitFromAppLayout)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return presenter.handleNavigationItemSelected(item)
    }

    fun showFragment(
        fragment: Fragment,
        fragmentTag: String,
        clearToTag: String?,
        isInclusive: Boolean = false
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

    fun getFragmentByTag(tag: String) =
        supportFragmentManager.findFragmentByTag(tag)

    fun selectItemMenu(itemId: Int) {
        navigationView?.setCheckedItem(itemId)
    }

    fun closeDrawer() {
        if (drawerLayout?.isDrawerVisible(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
            return
        }
    }

    fun closeActivity() {
        finish()
    }

    fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
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
        presenter.logOutFromApp()
    }

    override fun createPresenter() = MainActivityPresenter(this)
    fun getSharedPreference() = getRunningSharedPreference()
    fun setUserNameOnNavHeader(name: String) {
        navHeaderUserNameTextView?.text = name
    }

    fun setUserEmailOnNavHeader(email: String) {
        navHeaderUserEmailTextView?.text = email
    }

    fun setTitleActionBar(resId: Int) {
        supportActionBar?.title = getString(resId)
    }

    override fun onDestroy() {
        drawerToggle = null

        super.onDestroy()
    }
}