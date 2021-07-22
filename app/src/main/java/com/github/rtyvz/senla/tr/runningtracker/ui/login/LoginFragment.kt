package com.github.rtyvz.senla.tr.runningtracker.ui.login

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
import com.github.rtyvz.senla.tr.runningtracker.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class LoginFragment : Fragment() {

    companion object {
        private const val EMPTY_STRING = ""
        private const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"

        val TAG = LoginFragment::class.java.simpleName.toString()

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registrationActionTextView: MaterialTextView
    private lateinit var errorTextView: MaterialTextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)
        moveToRegistration()

        loginButton.setOnClickListener {
            checkEnteredValues()
        }
        registrationActionTextView.paint.isUnderlineText = true
    }

    private fun moveToRegistration() {
        registrationActionTextView.setOnClickListener {
            errorTextView.text = EMPTY_STRING
            (activity as LoginFlowContract).openRegistrationFragment()
        }
    }

    private fun findViews(view: View) {
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        errorTextView = view.findViewById(R.id.errorTextView)
        registrationActionTextView = view.findViewById(R.id.registrationActionTextView)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun isEmailInvalid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkEnteredValues() {
        when {
            emailEditText.text.isNullOrBlank()
                    or passwordEditText.text.isNullOrBlank() -> errorTextView.text =
                getString(R.string.login_fragment_empty_fields_error)
            isEmailInvalid(emailEditText.text.toString()) -> errorTextView.text =
                getString(R.string.login_fragment_match_email_error)
            else -> {
                registrationActionTextView.isEnabled = false
                errorTextView.text = EMPTY_STRING
                sendLoginRequest(
                    UserDataRequest(
                        email = emailEditText.text.toString(),
                        password = passwordEditText.text.toString()
                    )
                )
            }
        }
    }

    private fun sendLoginRequest(userDataRequest: UserDataRequest) {
        progressBar.isVisible = true
        App.loginFlowRepository.loginUser(userDataRequest, emailEditText.text.toString()) {
            progressBar.isVisible = false

            when (it) {
                is Result.Success -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    (activity as HandleClosingActivityContract).finishActivity()
                }
                is Result.Error -> {
                    registrationActionTextView.isEnabled = true
                    when (it.error) {
                        INVALID_CREDENTIALS -> {
                            errorTextView.text =
                                getString(R.string.login_fragment_invalid_credentials)
                        }
                        else -> errorTextView.text =
                            getString(R.string.login_fragment_unknown_network_error)
                    }
                }
            }
        }
    }
}