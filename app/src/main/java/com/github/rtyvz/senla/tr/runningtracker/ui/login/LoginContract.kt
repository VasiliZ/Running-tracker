package com.github.rtyvz.senla.tr.runningtracker.ui.login

import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface LoginContract {
    interface ViewLogin : MainContract.View {
        fun showErrorMessage(message: String)
        fun openRegistrationFragment()
        fun openMainActivity()
        fun clearError()
        fun getEmail(): String
        fun getPassword(): String
    }

    interface PresenterLogin : MainContract.Presenter<ViewLogin> {
        fun moveToRegistration()
        fun sendLoginRequest(userDataRequest: UserDataRequest, email: String)
        fun checkUserInput()
    }
}