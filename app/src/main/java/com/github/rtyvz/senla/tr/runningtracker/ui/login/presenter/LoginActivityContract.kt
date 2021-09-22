package com.github.rtyvz.senla.tr.runningtracker.ui.login.presenter

import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface LoginActivityContract {

    interface LoginActivityView : MainContract.View {
        fun showFragment(fragment: Fragment, fragmentTag: String, clearToTag: String?)
    }

    interface LoginActivityPresenter : MainContract.Presenter<LoginActivityView> {
        fun openRegistrationFragment()
        fun openLoginFragment()
    }
}