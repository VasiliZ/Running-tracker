package com.github.rtyvz.senla.tr.runningtracker.ui.registration.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.registration.RegistrationFragment

class RegistrationPresenter(private val view: RegistrationFragment) : BasePresenter<BaseView>(view){

    companion object {
        private const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"
    }

    fun openLoginFragment() {
        view.clearError()
        view.moveToLoginFragment()
    }

    fun checkInputData() {
        when {
            view.getEmail().isBlank()
                    or view.getName().isBlank()
                    or view.getLastName().isBlank()
                    or view.getPassword().isBlank()
                    or view.getRepeatedPassword().isBlank() ->
                view.showMessage(R.string.registration_fragment_empty_fields_error)
            isEmailInvalid(view.getEmail()) -> view.showMessage(R.string.registration_fragment_match_email_error)
            isPasswordsNotEquals(
                view.getPassword(),
                view.getRepeatedPassword()
            ) ->
                view.showMessage(R.string.registration_fragment_password_matches_error)
            else -> {
                sendRegistrationRequest(
                    UserDataRequest(
                        view.getEmail(),
                        view.getName(),
                        view.getLastName(),
                        view.getPassword()
                    )
                )
            }
        }
    }

    private fun isEmailInvalid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordsNotEquals(password: String, repeatPassword: String): Boolean {
        return password != repeatPassword
    }

    private fun sendRegistrationRequest(userDataRequest: UserDataRequest) {
        view.showLoading()
        App.loginFlowRepository.registerUser(userDataRequest) {
            view.hideLoading()
            when (it) {
                is Result.Success -> {
                    view.openMainActivity()
                }
                is Result.Error -> {
                    when (it.error) {
                        EMAIL_ALREADY_EXISTS -> {
                            view.showMessage(R.string.registration_fragment_email_already_exists_response)
                        }
                        else -> {
                            view.showMessage(R.string.registration_fragment_unknown_network_error)
                        }
                    }
                    view.showMessage(it.error)
                }
            }
        }
    }
}