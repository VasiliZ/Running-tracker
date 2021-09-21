package com.github.rtyvz.senla.tr.runningtracker.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class LoginFragment : BaseFragment<LoginContract.PresenterLogin, LoginContract.ViewLogin>(), LoginContract.ViewLogin {

    companion object {
        private const val EMPTY_STRING = ""
        private const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"

        val TAG = LoginFragment::class.java.simpleName.toString()

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private var emailEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null
    private var loginButton: MaterialButton? = null
    private var registrationActionTextView: MaterialTextView? = null
    private var errorTextView: MaterialTextView? = null
    private var progressBar: ProgressBar? = null

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

        loginButton?.setOnClickListener {
            checkEnteredValues()
        }

        registrationActionTextView?.setOnClickListener {
            errorTextView?.text = EMPTY_STRING
            getPresenter().moveToRegistration()
        }

        registrationActionTextView?.paint?.isUnderlineText = true
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
            emailEditText?.text.isNullOrBlank()
                    or passwordEditText?.text.isNullOrBlank() -> errorTextView?.text =
                    getString(R.string.login_fragment_empty_fields_error)
            isEmailInvalid(emailEditText?.text.toString()) -> errorTextView?.text =
                    getString(R.string.login_fragment_match_email_error)
            else -> {
                registrationActionTextView?.isEnabled = false
                errorTextView?.text = EMPTY_STRING
                getPresenter().sendLoginRequest(
                        UserDataRequest(
                                email = emailEditText?.text.toString(),
                                password = passwordEditText?.text.toString()
                        ), emailEditText?.text.toString()
                )
            }
        }
    }

    override fun createPresenter(): LoginContract.PresenterLogin {
        return LoginPresenter()
    }

    override fun showErrorMessage(message: String) {
        when (message) {
            INVALID_CREDENTIALS -> {
                errorTextView?.text = getString(R.string.login_fragment_invalid_credentials)
            }
            else -> getString(R.string.login_fragment_unknown_network_error)
        }
    }

    override fun openRegistrationFragment() {
        (activity as ChangeFragmentContract).openRegistrationFragment()
    }

    override fun openMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        activity?.finish()
    }

    override fun showLoading() {
        progressBar?.isVisible = true
    }

    override fun hideLoading() {
        progressBar?.isVisible = false
    }

    override fun onDestroyView() {
        emailEditText = null
        passwordEditText = null
        loginButton = null
        registrationActionTextView = null
        errorTextView = null
        progressBar = null

        super.onDestroyView()
    }
}