package com.github.rtyvz.senla.tr.runningtracker.providers

import bolts.CancellationToken
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.tasks.FetchTracksTask
import com.github.rtyvz.senla.tr.runningtracker.tasks.LoginUserTask
import com.github.rtyvz.senla.tr.runningtracker.tasks.RegisterUserTask
import com.github.rtyvz.senla.tr.runningtracker.tasks.SaveUserDataTask

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

    fun getFetchingTrackTask(tracksRequest: TracksRequest, cancellationToken: CancellationToken) =
        FetchTracksTask().fetchTracks(tracksRequest, cancellationToken)
}