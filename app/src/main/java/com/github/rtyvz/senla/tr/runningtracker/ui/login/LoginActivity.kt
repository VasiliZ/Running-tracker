package com.github.rtyvz.senla.tr.runningtracker.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.login.presenter.LoginActivityPresenter

class LoginActivity : BaseActivity<LoginActivityPresenter>(), BaseView,
    ChangeFragmentContract {

    companion object {
        private const val BACK_STACK_SIZE_1 = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter.onCreate()
    }

    override fun openLoginFragment() {
        presenter.onOpenLoginFragment()
    }

    override fun openRegistrationFragment() {
        presenter.onOpenRegistrationFragment()
    }

    override fun onBackPressed() {
        when (supportFragmentManager.backStackEntryCount) {
            BACK_STACK_SIZE_1 -> {
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun createPresenter(): LoginActivityPresenter {
        return LoginActivityPresenter(this)
    }

    fun showFragment(
        fragment: Fragment,
        fragmentTag: String,
        clearToTag: String?
    ) {
        if (clearToTag != null)
            supportFragmentManager.popBackStack(
                clearToTag,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )

        supportFragmentManager.beginTransaction()
            .replace(R.id.loginFlowContainer, fragment, fragmentTag)
            .addToBackStack(fragmentTag)
            .commit()
    }
}