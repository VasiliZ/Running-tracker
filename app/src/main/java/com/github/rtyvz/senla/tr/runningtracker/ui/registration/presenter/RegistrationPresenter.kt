package com.github.rtyvz.senla.tr.runningtracker.ui.registration.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.entity.Result

class RegistrationPresenter : BasePresenter<RegistrationContract.ViewRegistration>(),
    RegistrationContract.PresenterRegistration {

    companion object {
        private const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"
    }

    override fun openLoginFragment() {
        getView().clearError()
        getView().moveToLoginFragment()
    }

    override fun checkInputData() {
        when {
            getView().getEmail().isBlank()
                    or getView().getName().isBlank()
                    or getView().getLastName().isBlank()
                    or getView().getPassword().isBlank()
                    or getView().getRepeatedPassword().isBlank() ->
                getView().showMessage(R.string.registration_fragment_empty_fields_error)
            isEmailInvalid(getView().getEmail()) -> getView().showMessage(R.string.registration_fragment_match_email_error)
            isPasswordsNotEquals(
                getView().getPassword(),
                getView().getRepeatedPassword()
            ) ->
                getView().showMessage(R.string.registration_fragment_password_matches_error)
            else -> {
                sendRegistrationRequest(
                    UserDataRequest(
                        getView().getEmail(),
                        getView().getName(),
                        getView().getLastName(),
                        getView().getPassword()
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
        getView().showLoading()
        App.loginFlowRepository.registerUser(userDataRequest) {
            getView().hideLoading()
            when (it) {
                is Result.Success -> {
                    getView().openMainActivity()
                }
                is Result.Error -> {
                    when (it.error) {
                        EMAIL_ALREADY_EXISTS -> {
                            getView().showMessage(R.string.registration_fragment_email_already_exists_response)
                        }
                        else -> {
                            getView().showMessage(R.string.registration_fragment_unknown_network_error)
                        }
                    }
                    getView().showMessage(it.error)
                }
            }
        }
    }
}