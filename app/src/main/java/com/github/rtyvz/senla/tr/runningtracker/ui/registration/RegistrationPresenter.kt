package com.github.rtyvz.senla.tr.runningtracker.ui.registration

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.entity.Result

class RegistrationPresenter : BasePresenter<RegistrationContract.ViewRegistration>(),
    RegistrationContract.PresenterRegistration {

    override fun sendRegistrationRequest(userDataRequest: UserDataRequest) {
        getView().showLoading()
        App.loginFlowRepository.authUser(userDataRequest) {
            getView().hideLoading()
            when (it) {
                is Result.Success -> {
                    getView().openMainActivity()
                }
                is Result.Error -> {
                    getView().showMessage(it.error)
                }
            }
        }
    }

    override fun openLoginFragment() {
        getView().moveToLoginFragment()
    }
}