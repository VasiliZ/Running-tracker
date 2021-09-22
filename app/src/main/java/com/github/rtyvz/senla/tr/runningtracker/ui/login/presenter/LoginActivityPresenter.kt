package com.github.rtyvz.senla.tr.runningtracker.ui.login.presenter

import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.registration.RegistrationFragment

class LoginActivityPresenter : BasePresenter<LoginActivityContract.LoginActivityView>(),
    LoginActivityContract.LoginActivityPresenter {

    override fun openRegistrationFragment() {
        getView().showFragment(
            RegistrationFragment.newInstance(),
            RegistrationFragment.TAG,
            LoginFragment.TAG
        )
    }

    override fun openLoginFragment() {
        getView().showFragment(
            LoginFragment.newInstance(),
            LoginFragment.TAG,
            RegistrationFragment.TAG
        )
    }
}