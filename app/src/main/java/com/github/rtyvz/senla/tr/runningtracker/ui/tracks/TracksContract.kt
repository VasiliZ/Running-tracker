package com.github.rtyvz.senla.tr.runningtracker.ui.tracks

import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface TracksContract {

    interface ViewTracks : MainContract.View {}

    interface PresenterTracks : MainContract.Presenter<ViewTracks> {}
}