package com.github.rtyvz.senla.tr.runningtracker.ui.track.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter

class TrackPresenter : BasePresenter<TrackContract.ViewTrack>(), TrackContract.PresenterTrack {

    override fun getPoints(track: TrackEntity?) {
        if (track != null) {
            App.mainRunningRepository.getTrackPoints(track.id, track.beginsAt) {
                when (it) {
                    is Result.Success -> {
                        val trackPoints = it.data.listPoints
                        if (trackPoints.isNotEmpty()) {
                            getView().clearMap()
                            getView().setupMapData(trackPoints)
                            getView().drawPath(trackPoints)
                            getView().setDataOnUI(track)
                        }
                    }
                    is Result.Error -> {
                        getView().showError()
                    }
                }
            }
        }
    }
}