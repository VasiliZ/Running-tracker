package com.github.rtyvz.senla.tr.runningtracker.ui.tracks.presenter

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BasePresenter

class TracksPresenter : BasePresenter<TracksContract.ViewTracks>(), TracksContract.PresenterTracks {

    companion object {
        private const val INVALID_TOKEN = "INVALID_TOKEN"
        const val GET_POINTS_ERROR = "GET_POINTS_ERROR"
        const val EMPTY_DATA_RESULT = "EMPTY_DATA_RESULT"
    }

    override fun openRunningActivity() {
        getView().startRunningActivity()
    }

    override fun getTracksFromDb() {
        App.mainRunningRepository.getTracksFromDb {
            App.state?.isDataLoadedYet = true
            getView().hideInformation()
            when (it) {
                is Result.Success -> {
                    getView().hideInformation()
                    getView().setData(it.data.tracksList)
                }
                is Result.Error -> {
                    when (it.error) {
                        INVALID_TOKEN -> {
                            getView().logout()
                        }
                        EMPTY_DATA_RESULT -> {
                            getView().showInformation()
                            getView().showMessage(R.string.tracks_fragment_havent_got_data_for_display)
                        }
                        else -> getView().showErrorResponseNextRunDialog()
                    }
                }
            }
        }
    }

    override fun getTracksFromDb(isDataLoadedYet: Boolean) {
        App.mainRunningRepository.getTracksFromDb(isDataLoadedYet) {
            if (it.tracksList.isEmpty()) {
                getView().showInformation()
                getView().showMessage(R.string.tracks_fragment_havent_got_data_for_display)
            } else {
                getView().hideInformation()
                getView().setData(it.tracksList)
            }
        }
    }

    override fun getTracksFromServer(token: String) {
        getView().hideInformation()
        getView().showLoading()

        App.mainRunningRepository.getTracks(TracksRequest(token)) {
            getView().hideLoading()
            getView().hideRefresh()
            App.state?.isDataLoadedYet = true
            when (it) {
                is Result.Error -> {
                    when (it.error) {
                        INVALID_TOKEN -> {
                            getView().logout()
                        }
                        else -> {
                            when (it.error) {
                                INVALID_TOKEN -> {
                                    getView().logout()
                                }
                                GET_POINTS_ERROR -> {
                                    getView().showErrorFetchingPointsDialog()
                                }
                                else -> {
                                    getView().showErrorResponseFirstRunDialog()
                                }
                            }
                        }
                    }
                }
                is Result.Success -> {
                    if (it.data.tracksList.isEmpty()) {
                        getView().showInformation()
                        getView().showMessage(R.string.tracks_fragment_havent_got_data_for_display)
                    } else {
                        getView().hideInformation()
                        getView().setData(it.data.tracksList)
                    }
                }
            }
        }
    }
}