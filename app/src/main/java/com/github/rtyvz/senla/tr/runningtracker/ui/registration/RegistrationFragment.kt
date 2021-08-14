package com.github.rtyvz.senla.tr.runningtracker.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.OnCloseActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginFlowContract
import com.github.rtyvz.senla.tr.runningtracker.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class RegistrationFragment : Fragment() {

    companion object {
        val TAG = RegistrationFragment::class.java.simpleName.toString()
        private const val EMPTY_STRING = ""
        private const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"

        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }

    private lateinit var emailEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var repeatPasswordEditText: TextInputEditText
    private lateinit var registrationButton: MaterialButton
    private lateinit var loginActionTextView: MaterialTextView
    private lateinit var errorTextView: MaterialTextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)
        moveToLogin()

        loginActionTextView.paint.isUnderlineText = true
        registrationButton.setOnClickListener {
            checkEnteredValues()
        }
    }

    private fun moveToLogin() {
        loginActionTextView.setOnClickListener {
            errorTextView.text = EMPTY_STRING
            (activity as LoginFlowContract).openLoginFragment()
        }
    }

    private fun findViews(view: View) {
        emailEditText = view.findViewById(R.id.emailEditText)
        nameEditText = view.findViewById(R.id.nameEditText)
        lastNameEditText = view.findViewById(R.id.lastNameEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        repeatPasswordEditText = view.findViewById(R.id.repeatPasswordEditText)
        registrationButton = view.findViewById(R.id.registrationButton)
        loginActionTextView = view.findViewById(R.id.loginActionTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun isEmailInvalid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordsNotEquals(password: String, repeatPassword: String): Boolean {
        return password != repeatPassword
    }

    private fun checkEnteredValues() {
        when {
            emailEditText.text.isNullOrBlank()
                    or nameEditText.text.isNullOrBlank()
                    or lastNameEditText.text.isNullOrBlank()
                    or passwordEditText.text.isNullOrBlank()
                    or repeatPasswordEditText.text.isNullOrBlank() -> errorTextView.text =
                getString(R.string.registration_fragment_empty_fields_error)
            isEmailInvalid(emailEditText.text.toString()) -> errorTextView.text =
                getString(R.string.registration_fragment_match_email_error)
            isPasswordsNotEquals(
                passwordEditText.text.toString(),
                repeatPasswordEditText.text.toString()
            ) ->
                errorTextView.text =
                    getString(R.string.registration_fragment_password_matches_error)
            else -> {
                errorTextView.text = EMPTY_STRING
                loginActionTextView.isEnabled = false
                sendRegistrationRequest(
                    UserDataRequest(
                        emailEditText.text.toString(),
                        nameEditText.text.toString(),
                        lastNameEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                )
            }
        }
    }

    private fun sendRegistrationRequest(userDataRequest: UserDataRequest) {
        progressBar.isVisible = true
        App.loginFlowRepository.authUser(userDataRequest) {
            progressBar.isVisible = false

            when (it) {
                is Result.Success -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    (activity as OnCloseActivityContract).closeActivity()
                }
                is Result.Error -> {
                    loginActionTextView.isEnabled = true
                    when (it.error) {
                        EMAIL_ALREADY_EXISTS -> {
                            errorTextView.text =
                                getString(R.string.registration_fragment_email_already_exists_response)
                        }
                        else -> {
                            errorTextView.text =
                                getString(R.string.registration_fragment_unknown_network_error)
                        }
                    }
                }
            }
        }
    }
}