package com.github.rtyvz.senla.tr.runningtracker.providers

import android.location.Location
import bolts.CancellationToken
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
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

    fun getInsertTrackIntoDbkTask(
        cancellationToken: CancellationToken,
        listTrack: List<TrackEntity>
    ) =
        InsertTracksIntoDbTask().insertTracksIntiDb(listTrack, cancellationToken)

    fun getInsertLocationTask(
        cancellationToken: CancellationToken,
        location: Location,
        beginAt: Long
    ) =
        InsertLocationIntoDbTask().insertLocation(location, beginAt, cancellationToken)

    fun getSaveTrackOnRemoteServerTask(
        track: TrackEntity,
        listPoints: List<Point>,
        cancellationToken: CancellationToken
    ) =
        SaveTrackOnRemoteServerTask().saveTrackOnRemoteServer(
            track,
            listPoints,
            cancellationToken
        )

    fun getUpdateTrackIntoDb(track: TrackEntity, cancellationToken: CancellationToken) =
        UpdateTackIntoDbTask().updateTrackIntoDb(track, cancellationToken)

    fun getDeleteTrackFromDbTask(cancellationToken: CancellationToken, condition: String) =
        RemoveTrackFromDbTask().removeTrackFromDb(cancellationToken, condition)
}