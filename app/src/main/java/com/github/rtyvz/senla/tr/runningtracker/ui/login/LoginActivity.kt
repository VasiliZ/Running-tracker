package com.github.rtyvz.senla.tr.runningtracker.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.OnCloseActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.registration.RegistrationFragment

class LoginActivity : AppCompatActivity(), LoginFlowContract, OnCloseActivityContract {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        openRegistrationFragment()
    }

    override fun openRegistrationFragment() {
        val registrationFragment =
            supportFragmentManager.findFragmentByTag(RegistrationFragment.TAG)
        val loginFragment = supportFragmentManager.findFragmentByTag(LoginFragment.TAG)

        if (registrationFragment is RegistrationFragment && loginFragment != null) {
            hideFragment(loginFragment)
            showFragment(registrationFragment)
        } else {
            addFragment(RegistrationFragment.newInstance(), RegistrationFragment.TAG)
        }
    }

    override fun openLoginFragment() {
        val registrationFragment =
            supportFragmentManager.findFragmentByTag(RegistrationFragment.TAG)
        val loginFragment = supportFragmentManager.findFragmentByTag(LoginFragment.TAG)

        if (loginFragment is LoginFragment && registrationFragment != null) {
            hideFragment(registrationFragment)
            showFragment(loginFragment)
        } else {
            if (registrationFragment != null) {
                hideFragment(registrationFragment)
            }
            addFragment(LoginFragment.newInstance(), LoginFragment.TAG)
        }
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.loginFlowContainer, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment).commit()
    }

    private fun hideFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(fragment).commit()
    }

    override fun onBackPressed() {
        when (supportFragmentManager.backStackEntryCount) {
            1, 2 -> {
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun closeActivity() {
        finish()
    }
}