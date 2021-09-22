package com.github.rtyvz.senla.tr.runningtracker.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseActivity

class LoginActivity :
    BaseActivity<LoginActivityContract.LoginActivityPresenter, LoginActivityContract.LoginActivityView>(),
    LoginActivityContract.LoginActivityView,
    ChangeFragmentContract {

    companion object {
        private const val BACK_STACK_SIZE_1 = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        getPresenter().openRegistrationFragment()
    }

    override fun openRegistrationFragment() {
        getPresenter().openRegistrationFragment()
    }

    override fun openLoginFragment() {
        getPresenter().openLoginFragment()
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

    override fun createPresenter(): LoginActivityContract.LoginActivityPresenter {
        return LoginActivityPresenter()
    }

    override fun showFragment(
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

    override fun showLoading() {
        //nothing to do here
    }

    override fun hideLoading() {
        //nothing to do here
    }
}