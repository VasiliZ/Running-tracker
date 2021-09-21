package com.github.rtyvz.senla.tr.runningtracker.ui.login

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter

class LoginPresenter : BasePresenter<LoginContract.ViewLogin>(), LoginContract.PresenterLogin {

    override fun moveToRegistration() {
        getView().openRegistrationFragment()
    }

    override fun sendLoginRequest(userDataRequest: UserDataRequest, email: String) {
        getView().showLoading()
        App.loginFlowRepository.loginUser(userDataRequest, email) {
            getView().hideLoading()
            when (it) {
                is Result.Success -> {
                    getView().openMainActivity()
                }
                is Result.Error -> {
                    getView().showErrorMessage(it.error)
                }
            }
        }
    }
}