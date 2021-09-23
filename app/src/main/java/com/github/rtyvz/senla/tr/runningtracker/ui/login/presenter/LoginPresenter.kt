package com.github.rtyvz.senla.tr.runningtracker.ui.login.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginFragment

class LoginPresenter(view: LoginFragment) : BasePresenter<LoginContract.ViewLogin>(view),
    LoginContract.PresenterLogin {

    companion object {
        private const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    }

    override fun moveToRegistration() {
        getView().clearError()
        getView().openRegistrationFragment()
    }

    override fun checkUserInput() {
        when {
            getView().getEmail().isBlank() or getView().getPassword().isBlank()
            -> getView().showErrorMessage(R.string.login_fragment_empty_fields_error)
            isEmailInvalid(getView().getEmail()) -> getView().showErrorMessage(R.string.login_fragment_match_email_error)
            else -> {
                getView().clearError()
                sendLoginRequest(
                    UserDataRequest(
                        email = getView().getEmail(),
                        password = getView().getPassword()
                    ), getView().getEmail()
                )
            }
        }
    }

    private fun isEmailInvalid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendLoginRequest(userDataRequest: UserDataRequest, email: String) {
        getView().showLoading()
        App.loginFlowRepository.loginUser(userDataRequest, email) {
            getView().hideLoading()
            when (it) {
                is Result.Success -> {
                    getView().openMainActivity()
                }
                is Result.Error -> {
                    when (it.error) {
                        INVALID_CREDENTIALS -> {
                            getView().showErrorMessage(R.string.login_fragment_invalid_credentials)
                        }
                        else ->
                            getView().showErrorMessage(R.string.login_fragment_unknown_network_error)
                    }
                }
            }
        }
    }
}