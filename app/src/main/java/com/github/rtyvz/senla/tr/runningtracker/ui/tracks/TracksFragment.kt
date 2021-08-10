package com.github.rtyvz.senla.tr.runningtracker.ui.tracks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.HandleClosingActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs.ErrorFetchingPointsDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs.ErrorResponseFirstRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs.ErrorResponseNextRunDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class TracksFragment : Fragment() {

    companion object {
        val TAG: String = TracksFragment::class.java.simpleName.toString()
        private const val EMPTY_STRING = ""
        const val GET_POINTS_ERROR = "GET_POINTS_ERROR"
        const val EMPTY_DATA_RESULT = "EMPTY_DATA_RESULT"
        private const val USER_TOKEN = "USER_TOKEN"
        private const val INVALID_TOKEN = "INVALID_TOKEN"
        private const val EXTRA_IS_FIRST_TIME_RUN_APP = "IS_FIRST_TIME_RUN_APP"
        private const val FIRST_ITEM_FOR_SCROLL = 0

        fun newInstance(isFirstTimeRunAppFlag: Boolean): TracksFragment {
            return TracksFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_FIRST_TIME_RUN_APP, isFirstTimeRunAppFlag)
                }
            }
        }
    }

    private lateinit var informationTextView: MaterialTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var fab: FloatingActionButton
    private lateinit var listTrackRecycler: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val runningAdapter by lazy {
        TracksAdapter {
            (parentFragment as OnItemClickListListener).onTrackItemClick(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tracks_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)

        val token = requireActivity().getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)

        if (token?.isNotBlank() == true && arguments?.getBoolean(EXTRA_IS_FIRST_TIME_RUN_APP) != false) {
            getTrackFromServer(token)
        } else {
            if (App.state?.listTracks?.isEmpty() == true) {
                getTracksFromDb()
            } else {
                restoreData()
            }
        }

        fab.setOnClickListener {
            startActivity(Intent(requireContext(), RunningActivity::class.java))
        }

        swipeRefreshLayout.setOnRefreshListener {
            if (token != null && token.isNotBlank()) {
                getTrackFromServer(token)
            }
        }
        listTrackRecycler.adapter = runningAdapter

    }

    private fun restoreData() {
        runningAdapter.submitList(App.state?.listTracks)
    }

    private fun findViews(view: View) {
        informationTextView = view.findViewById(R.id.informationTextView)
        progressBar = view.findViewById(R.id.progressBar)
        fab = view.findViewById(R.id.fab)
        listTrackRecycler = view.findViewById(R.id.tracksListRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout)
    }

    private fun getTrackFromServer(token: String) {
        progressBar.isVisible = true
        App.mainRunningRepository.getTracks(TracksRequest(token)) {
            progressBar.isVisible = false
            swipeRefreshLayout.isRefreshing = false
            when (it) {
                is Result.Error -> {
                    when (it.error) {
                        INVALID_TOKEN -> {
                            requireContext().getSharedPreference().edit().clear().apply()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            (activity as HandleClosingActivityContract).closeActivity()
                        }
                        else -> {
                            when (it.error) {
                                INVALID_TOKEN -> {
                                    (activity as LogOutFromApp).logout()
                                }
                                GET_POINTS_ERROR -> {
                                    ErrorFetchingPointsDialog.newInstance().show(
                                        childFragmentManager, ErrorFetchingPointsDialog.TAG
                                    )
                                }
                                else -> {
                                    ErrorResponseFirstRunDialog.newInstance()
                                        .show(childFragmentManager, ErrorResponseFirstRunDialog.TAG)
                                }
                            }
                        }
                    }
                }
                is Result.Success -> {
                    if (it.data.tracksList.isEmpty()) {
                        informationTextView.text =
                            getString(R.string.tracks_fragment_havent_got_data_for_display)
                    } else {
                        informationTextView.isVisible = false
                        runningAdapter.submitList(it.data.tracksList)
                    }
                }
            }
        }
    }

    fun getTracksFromDb() {
        App.mainRunningRepository.getTracksFromDb {
            when (it) {
                is Result.Success -> {
                    App.state?.listTracks = it.data.tracksList
                    informationTextView.isVisible = false
                    runningAdapter.submitList(it.data.tracksList)
                    listTrackRecycler.layoutManager?.scrollToPosition(
                        App.state?.firstVisibleItemPosition ?: FIRST_ITEM_FOR_SCROLL
                    )
                }
                is Result.Error -> {
                    when (it.error) {
                        INVALID_TOKEN -> {
                            (activity as LogOutFromApp).logout()
                        }
                        EMPTY_DATA_RESULT -> {
                            informationTextView.text =
                                getString(R.string.tracks_fragment_havent_got_data_for_display)
                        }
                        else ->
                            ErrorResponseNextRunDialog.newInstance()
                                .show(childFragmentManager, ErrorResponseNextRunDialog.TAG)
                    }
                }
            }
        }
    }

    interface OnItemClickListListener {
        fun onTrackItemClick(trackEntity: TrackEntity)
    }

    interface LogOutFromApp {
        fun logout()
    }

    fun retryRequest() {
        val token = requireActivity().getSharedPreference().getString(USER_TOKEN, EMPTY_STRING)
        if (token != null && token.isNotBlank()) {
            getTrackFromServer(token)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        App.state?.firstVisibleItemPosition =
            (listTrackRecycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        super.onSaveInstanceState(outState)
    }
}