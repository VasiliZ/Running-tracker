package com.github.rtyvz.senla.tr.runningtracker.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class LoginFragment : Fragment() {

    companion object {
        val TAG = LoginFragment::class.java.simpleName.toString()

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private lateinit var emailEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registrationActionTextView: MaterialTextView

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

        registrationActionTextView.paint.isUnderlineText = true
    }

    private fun moveToRegistration() {
        registrationActionTextView.setOnClickListener {
            (activity as LoginFlowContract).openRegistrationFragment()
        }
    }

    private fun findViews(view: View) {
        emailEditText = view.findViewById(R.id.emailEditText)
        nameEditText = view.findViewById(R.id.nameEditText)
        loginButton = view.findViewById(R.id.loginButton)
        registrationActionTextView = view.findViewById(R.id.registrationActionTextView)
    }
}