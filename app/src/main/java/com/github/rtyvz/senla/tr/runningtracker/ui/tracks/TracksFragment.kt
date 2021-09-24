package com.github.rtyvz.senla.tr.runningtracker.ui.tracks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs.ErrorFetchingPointsDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs.ErrorResponseFirstRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs.ErrorResponseNextRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.presenter.TracksPresenter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class TracksFragment : BaseFragment<TracksPresenter>(),
    BaseView, ErrorResponseNextRunDialog.ErrorResponseDialogCallBack,
    ErrorResponseFirstRunDialog.ErrorResponseDialogCallBack {

    companion object {
        val TAG: String = TracksFragment::class.java.simpleName.toString()
        private const val EMPTY_STRING = ""
        private const val EXTRA_IS_FIRST_TIME_RUN_APP = "IS_FIRST_TIME_RUN_APP"
        private const val FIRST_ITEM_LIST = 0
        private const val USER_TOKEN = "USER_TOKEN"

        fun newInstance(isFirstTimeRunAppFlag: Boolean): TracksFragment {
            return TracksFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_FIRST_TIME_RUN_APP, isFirstTimeRunAppFlag)
                }
            }
        }
    }

    private var informationTextView: MaterialTextView? = null
    private var progressBar: ProgressBar? = null
    private var fab: FloatingActionButton? = null
    private var listTrackRecycler: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var coordinateLayout: CoordinatorLayout? = null

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

        initViews(view)

        val token =
            requireActivity().getRunningSharedPreference().getString(USER_TOKEN, EMPTY_STRING)

        if (token?.isNotBlank() == true && arguments?.getBoolean(EXTRA_IS_FIRST_TIME_RUN_APP) != false) {
            presenter.onGetTracksFromServer(token)
        } else {
            if (App.state?.isDataLoadedYet == false) {
                presenter.onGetTracksFromDb()
            }
        }

        fab?.setOnClickListener {
            presenter.onFabClicked()
        }

        swipeRefreshLayout?.setOnRefreshListener {
            if (token != null && token.isNotBlank()) {
                presenter.onGetTracksFromServer(token)
            }
        }

        listTrackRecycler?.adapter = runningAdapter
        runningAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (listTrackRecycler?.layoutManager as LinearLayoutManager).scrollToPosition(
                    FIRST_ITEM_LIST
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()

        presenter.onGetTracksFromDb(App.state?.isDataLoadedYet == true)
    }

    private fun initViews(view: View) {
        coordinateLayout = view.findViewById(R.id.coordinatorLayout)
        informationTextView = view.findViewById(R.id.informationTextView)
        progressBar = view.findViewById(R.id.progressBar)
        fab = view.findViewById(R.id.fab)
        listTrackRecycler = view.findViewById(R.id.tracksListRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout)
    }


    override fun retryRequestTracksDataFromDb() {
        presenter.onGetTracksFromDb()
    }

    interface OnItemClickListListener {
        fun onTrackItemClick(trackEntity: TrackEntity)
    }

    interface LogOutFromApp {
        fun logout()
    }

    override fun retryRequestTracksDataFromServer() {
        requireActivity().getRunningSharedPreference().getString(USER_TOKEN, EMPTY_STRING)?.let {
            presenter.onGetTracksFromServer(it)
        }
    }

    override fun createPresenter() = TracksPresenter(this)

    fun startRunningActivity() {
        startActivity(Intent(requireContext(), RunningActivity::class.java))
    }

    fun logout() {
        requireContext().getRunningSharedPreference().edit().clear().apply()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    fun showInformation() {
        informationTextView?.isVisible = true
    }

    fun hideInformation() {
        informationTextView?.isVisible = false
    }

    fun hideRefresh() {
        swipeRefreshLayout?.isRefreshing = false
    }

    fun showMessage(resId: Int) {
        informationTextView?.text = getString(resId)
    }

    fun setData(data: List<TrackEntity>) {
        runningAdapter.submitList(data)
    }

    fun showErrorFetchingPointsDialog() {
        ErrorFetchingPointsDialog.newInstance().show(
            childFragmentManager, ErrorFetchingPointsDialog.TAG
        )
    }

    fun showErrorResponseFirstRunDialog() {
        ErrorResponseFirstRunDialog.newInstance()
            .show(childFragmentManager, ErrorResponseFirstRunDialog.TAG)
    }

    fun showErrorResponseNextRunDialog() {
        ErrorResponseNextRunDialog.newInstance()
            .show(childFragmentManager, ErrorResponseNextRunDialog.TAG)
    }

    fun showLoading() {
        progressBar?.isVisible = true
    }

    fun hideLoading() {
        progressBar?.isVisible = false
    }

    override fun onDestroyView() {
        coordinateLayout = null
        informationTextView = null
        progressBar = null
        fab = null
        swipeRefreshLayout?.setOnRefreshListener(null)
        listTrackRecycler = null
        swipeRefreshLayout = null

        super.onDestroyView()
    }
}