package com.github.rtyvz.senla.tr.runningtracker.providers

import bolts.CancellationToken
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Track
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.tasks.*

object TasksProvider {
    fun getRegisterUserTask(
        userDataRequest: UserDataRequest,
        cancellationToken: CancellationToken
    ) =
        RegisterUserTask().registerUser(userDataRequest, cancellationToken)

    fun getLoginUserTask(userDataRequest: UserDataRequest, cancellationToken: CancellationToken) =
        LoginUserTask().loginUser(userDataRequest, cancellationToken)

    fun getSaveUserDataTask(userData: UserData, cancellationToken: CancellationToken) =
        SaveUserDataTask().saveUserData(userData, cancellationToken)

    fun getFetchingTrackFromNetworkTask(
        tracksRequest: TracksRequest,
        cancellationToken: CancellationToken
    ) =
        FetchTracksFromNetworkTask().fetchTracks(tracksRequest, cancellationToken)

    fun getInsertTrackIntoDbkTask(cancellationToken: CancellationToken, listTrack: List<Track>) =
        InsertTracksIntoDbTask().insertTracksIntiDb(listTrack, cancellationToken)
}