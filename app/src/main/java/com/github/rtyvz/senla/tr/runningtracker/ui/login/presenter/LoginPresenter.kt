package com.github.rtyvz.senla.tr.runningtracker.ui.login.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginFragment

class LoginPresenter(private val view: LoginFragment) :
    BasePresenter<BaseView>(view) {

    companion object {
        private const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    }

    fun onRegistrationTextViewClicked() {
       view.clearError()
        view.openRegistrationFragment()
    }

    fun onLoginButtonClicked() {
        when {
            view.getEmail().isBlank() or view.getPassword().isBlank()
            -> view.showErrorMessage(R.string.login_fragment_empty_fields_error)
            isEmailInvalid(view.getEmail()) -> view.showErrorMessage(R.string.login_fragment_match_email_error)
            else -> {
                view.clearError()
                sendLoginRequest(
                    UserDataRequest(
                        email = view.getEmail(),
                        password = view.getPassword()
                    ), view.getEmail()
                )
            }
        }
    }

    private fun isEmailInvalid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendLoginRequest(userDataRequest: UserDataRequest, email: String) {
        view.showLoading()
        App.loginFlowRepository.loginUser(userDataRequest, email) {
            view.hideLoading()
            when (it) {
                is Result.Success -> {
                    view.openMainActivity()
                }
                is Result.Error -> {
                    when (it.error) {
                        INVALID_CREDENTIALS -> {
                            view.showErrorMessage(R.string.login_fragment_invalid_credentials)
                        }
                        else ->
                            view.showErrorMessage(R.string.login_fragment_unknown_network_error)
                    }
                }
            }
        }
    }
}