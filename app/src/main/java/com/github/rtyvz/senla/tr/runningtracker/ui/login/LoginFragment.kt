package com.github.rtyvz.senla.tr.runningtracker.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.login.presenter.LoginPresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class LoginFragment : BaseFragment<LoginPresenter>(), BaseView {

    companion object {
        private const val EMPTY_STRING = ""

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
            presenter.checkUserInput()
        }

        registrationActionTextView?.setOnClickListener {
            presenter.moveToRegistration()
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

    override fun createPresenter(): LoginPresenter {
        return LoginPresenter(this)
    }

    fun showErrorMessage(resId: Int) {
        errorTextView?.text = getString(resId)
    }

    fun openRegistrationFragment() {
        (activity as ChangeFragmentContract).openRegistrationFragment()
    }

    fun openMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        activity?.finish()
    }

    fun clearError() {
        errorTextView?.text = EMPTY_STRING
    }

    fun getEmail() = emailEditText?.text.toString()
    fun getPassword() = passwordEditText?.text.toString()

    fun showLoading() {
        progressBar?.isVisible = true
    }

    fun hideLoading() {
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