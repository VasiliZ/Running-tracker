package com.github.rtyvz.senla.tr.runningtracker.ui.main

import android.content.Intent
import android.content.SharedPreferences
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
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.HandleClosingActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class MainFragment : Fragment() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName.toString()
        private const val EMPTY_STRING = ""
        private const val USER_TOKEN = "USER_TOKEN"
        private const val INVALID_TOKEN = "INVALID_TOKEN"
        private const val EXTRA_IS_FIRST_TIME_RUN_APP = "IS_FIRST_TIME_RUN_APP"


        fun newInstance(isFirstTimeRunAppFlag: Boolean): MainFragment {
            return MainFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_FIRST_TIME_RUN_APP, isFirstTimeRunAppFlag)
                }
            }
        }
    }

    private lateinit var informationTextView: MaterialTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)

        val sharedPrefs = requireActivity().getSharedPreference()
        val token = sharedPrefs.getString(USER_TOKEN, EMPTY_STRING)

        if (token?.isNotBlank() == true && arguments?.getBoolean(EXTRA_IS_FIRST_TIME_RUN_APP) != false) {
            getTrackFirstTime(token, sharedPrefs)
        } else {
            getTracksInOtherCases()
        }

        fab.setOnClickListener {
            startActivity(Intent(requireContext(), RunningActivity::class.java))
        }
    }

    private fun findViews(view: View) {
        informationTextView = view.findViewById(R.id.informationTextView)
        progressBar = view.findViewById(R.id.progressBar)
        fab = view.findViewById(R.id.fab)
    }

    private fun getTrackFirstTime(token: String, sharedPrefs: SharedPreferences) {
        progressBar.isVisible = true
        App.mainRunningRepository.getTracksFromNetwork(TracksRequest(token)) {
            progressBar.isVisible = false
            when (it) {
                is Result.Error -> {
                    when (it.error) {
                        INVALID_TOKEN -> {
                            sharedPrefs.edit().clear().apply()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            (activity as HandleClosingActivityContract).finishActivity()
                        }
                        else -> {
                            //todo set dialog with error here
                        }
                    }
                }
                is Result.Success -> {
                    if (it.responseBody.tracksList.isEmpty()) {
                        informationTextView.isVisible = true
                        informationTextView.text =
                            getString(R.string.main_fragment_havent_got_data_for_display)
                    } else {
                        //todo notify adapter
                    }
                }
            }
        }
    }

    private fun getTracksInOtherCases() {

    }
}