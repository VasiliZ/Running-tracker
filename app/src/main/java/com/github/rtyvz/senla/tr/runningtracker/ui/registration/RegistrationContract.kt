package com.github.rtyvz.senla.tr.runningtracker.ui.registration

import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface RegistrationContract {
    interface ViewRegistration : MainContract.View {
        fun openMainActivity()
        fun moveToLoginFragment()
        fun showMessage(message: String)
    }

    interface PresenterRegistration : MainContract.Presenter<ViewRegistration> {
        fun sendRegistrationRequest(userDataRequest: UserDataRequest)
        fun openLoginFragment()
    }
}