package com.github.rtyvz.senla.tr.runningtracker.ui.track.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.track.CurrentTrackFragment

class TrackPresenter(private val view: CurrentTrackFragment) : BasePresenter<BaseView>(view) {

    fun onTryToRetryPoints(track: TrackEntity?) {
        if (track != null) {
            App.mainRunningRepository.getTrackPoints(track.id, track.beginsAt) {
                when (it) {
                    is Result.Success -> {
                        val trackPoints = it.data.listPoints
                        if (trackPoints.isNotEmpty()) {
                            view.clearMap()
                            view.setupMapData(trackPoints)
                            view.drawPath(trackPoints)
                            view.setDataOnUI(track)
                        }
                    }
                    is Result.Error -> {
                        view.showError()
                    }
                }
            }
        }
    }
}