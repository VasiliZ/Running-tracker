package com.github.rtyvz.senla.tr.runningtracker.ui.tracks.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.tracks.TracksFragment

class TracksPresenter(private val view: TracksFragment) : BasePresenter<BaseView>(view){

    companion object {
        private const val INVALID_TOKEN = "INVALID_TOKEN"
        const val GET_POINTS_ERROR = "GET_POINTS_ERROR"
        const val EMPTY_DATA_RESULT = "EMPTY_DATA_RESULT"
    }

    fun onFabClicked() {
        view.startRunningActivity()
    }

    fun onGetTracksFromDb() {
        App.mainRunningRepository.getTracksFromDb {
            App.state?.isDataLoadedYet = true
            view.hideInformation()
            when (it) {
                is Result.Success -> {
                    view.hideInformation()
                    view.setData(it.data.tracksList)
                }
                is Result.Error -> {
                    when (it.error) {
                        INVALID_TOKEN -> {
                            view.logout()
                        }
                        EMPTY_DATA_RESULT -> {
                            view.showInformation()
                            view.showMessage(R.string.tracks_fragment_havent_got_data_for_display)
                        }
                        else -> view.showErrorResponseNextRunDialog()
                    }
                }
            }
        }
    }

    fun onGetTracksFromDb(isDataLoadedYet: Boolean) {
        App.mainRunningRepository.getTracksFromDb(isDataLoadedYet) {
            if (it.tracksList.isEmpty()) {
                view.showInformation()
                view.showMessage(R.string.tracks_fragment_havent_got_data_for_display)
            } else {
                view.hideInformation()
                view.setData(it.tracksList)
            }
        }
    }

    fun onGetTracksFromServer(token: String) {
        view.hideInformation()
        view.showLoading()

        App.mainRunningRepository.getTracks(TracksRequest(token)) {
            view.hideLoading()
            view.hideRefresh()
            App.state?.isDataLoadedYet = true
            when (it) {
                is Result.Error -> {
                    when (it.error) {
                        INVALID_TOKEN -> {
                            view.logout()
                        }
                        else -> {
                            when (it.error) {
                                INVALID_TOKEN -> {
                                    view.logout()
                                }
                                GET_POINTS_ERROR -> {
                                    view.showErrorFetchingPointsDialog()
                                }
                                else -> {
                                    view.showErrorResponseFirstRunDialog()
                                }
                            }
                        }
                    }
                }
                is Result.Success -> {
                    if (it.data.tracksList.isEmpty()) {
                        view.showInformation()
                        view.showMessage(R.string.tracks_fragment_havent_got_data_for_display)
                    } else {
                        view.hideInformation()
                        view.setData(it.data.tracksList)
                    }
                }
            }
        }
    }
}