package com.github.rtyvz.senla.tr.runningtracker.ui.registration.presenter

import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface RegistrationContract {
    interface ViewRegistration : MainContract.View {
        fun openMainActivity()
        fun moveToLoginFragment()
        fun getEmail(): String
        fun getName(): String
        fun getLastName(): String
        fun getPassword(): String
        fun getRepeatedPassword(): String
        fun showMessage(message: String)
        fun showMessage(resId: Int)
        fun clearError()
    }

    interface PresenterRegistration : MainContract.Presenter<ViewRegistration> {
        fun openLoginFragment()
        fun checkInputData()
    }
}