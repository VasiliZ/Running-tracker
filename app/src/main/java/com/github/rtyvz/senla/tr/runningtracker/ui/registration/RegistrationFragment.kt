package com.github.rtyvz.senla.tr.runningtracker.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.login.ChangeFragmentContract
import com.github.rtyvz.senla.tr.runningtracker.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class RegistrationFragment :
    BaseFragment<RegistrationContract.PresenterRegistration, RegistrationContract.ViewRegistration>(),
    RegistrationContract.ViewRegistration {

    companion object {
        val TAG = RegistrationFragment::class.java.simpleName.toString()
        private const val EMPTY_STRING = ""

        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }

    private var emailEditText: TextInputEditText? = null
    private var nameEditText: TextInputEditText? = null
    private var lastNameEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null
    private var repeatPasswordEditText: TextInputEditText? = null
    private var registrationButton: MaterialButton? = null
    private var loginActionTextView: MaterialTextView? = null
    private var errorTextView: MaterialTextView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        loginActionTextView?.paint?.isUnderlineText = true
        registrationButton?.setOnClickListener {
            getPresenter().checkInputData()
        }

        loginActionTextView?.setOnClickListener {
            errorTextView?.text = EMPTY_STRING
            getPresenter().openLoginFragment()
        }
    }

    private fun initViews(view: View) {
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

    override fun onDestroyView() {
        emailEditText = null
        nameEditText = null
        lastNameEditText = null
        passwordEditText = null
        repeatPasswordEditText = null
        registrationButton = null
        loginActionTextView = null
        errorTextView = null
        progressBar = null

        super.onDestroyView()
    }

    override fun createPresenter(): RegistrationContract.PresenterRegistration {
        return RegistrationPresenter()
    }

    override fun openMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        activity?.finish()
    }

    override fun moveToLoginFragment() {
        (activity as ChangeFragmentContract).openLoginFragment()
    }

    override fun getEmail() = emailEditText?.text.toString()
    override fun getName() = nameEditText?.text.toString()
    override fun getLastName() = lastNameEditText?.text.toString()
    override fun getPassword() = passwordEditText?.text.toString()
    override fun getRepeatedPassword() = repeatPasswordEditText?.text.toString()

    override fun showMessage(message: String) {
        errorTextView?.text = message
    }

    override fun clearError() {
        errorTextView?.text = EMPTY_STRING
    }

    override fun showLoading() {
        progressBar?.isVisible = true
    }

    override fun hideLoading() {
        progressBar?.isVisible = false
    }
}