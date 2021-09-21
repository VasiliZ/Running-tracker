package com.github.rtyvz.senla.tr.runningtracker.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.ClosableActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.registration.RegistrationFragment

class LoginActivity : AppCompatActivity(), ChangeFragmentContract {

    companion object {
        private const val BACK_STACK_SIZE_1 = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        openRegistrationFragment()
    }

    override fun openRegistrationFragment() {
        showFragment(
                RegistrationFragment.newInstance(),
                RegistrationFragment.TAG,
                LoginFragment.TAG
        )
    }

    override fun openLoginFragment() {
        showFragment(
                LoginFragment.newInstance(),
                LoginFragment.TAG,
                RegistrationFragment.TAG
        )
    }

    private fun showFragment(
            fragment: Fragment,
            fragmentTag: String,
            clearToTag: String? = null
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
}