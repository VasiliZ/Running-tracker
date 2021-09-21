package com.github.rtyvz.senla.tr.runningtracker.ui.track

import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface TrackContract {
    interface ViewTrack : MainContract.View {
        fun clearMap()
        fun setupMapData(trackPoints: List<PointEntity>)
        fun drawPath(trackPoints: List<PointEntity>)
        fun setDataOnUI(trackEntity: TrackEntity?)
        fun showError()
    }

    interface PresenterTrack : MainContract.Presenter<ViewTrack> {
        fun getPoints(track: TrackEntity?)

    }
}