package com.github.rtyvz.senla.tr.runningtracker.ui.login.presenter

import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.registration.RegistrationFragment

class LoginActivityPresenter(private val view: LoginActivity) : BasePresenter<BaseView>(view) {

    fun onOpenLoginFragment() {
        view.showFragment(
            LoginFragment.newInstance(),
            LoginFragment.TAG,
            RegistrationFragment.TAG
        )
    }

    fun onOpenRegistrationFragment() {
        view.showFragment(
            RegistrationFragment.newInstance(),
            RegistrationFragment.TAG,
            LoginFragment.TAG
        )
    }

    override fun onCreate() {
        view.showFragment(
            RegistrationFragment.newInstance(),
            RegistrationFragment.TAG,
            LoginFragment.TAG
        )
    }
}