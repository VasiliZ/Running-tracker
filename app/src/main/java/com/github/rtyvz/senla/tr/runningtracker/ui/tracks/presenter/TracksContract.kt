package com.github.rtyvz.senla.tr.runningtracker.ui.tracks.presenter

import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface TracksContract {

    interface ViewTracks : MainContract.View {
        fun startRunningActivity()
        fun logout()
        fun showInformation()
        fun hideInformation()
        fun hideRefresh()
        fun showMessage(resId: Int)
        fun setData(data: List<TrackEntity>)
        fun showErrorFetchingPointsDialog()
        fun showErrorResponseFirstRunDialog()
        fun showErrorResponseNextRunDialog()
    }

    interface PresenterTracks : MainContract.Presenter<ViewTracks> {
        fun openRunningActivity()
        fun getTracksFromDb()
        fun getTracksFromDb(isDataLoadedYet: Boolean)
        fun getTracksFromServer(token: String)
    }
}