package com.github.rtyvz.senla.tr.runningtracker.providers

import bolts.CancellationToken
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointsRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
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

    fun getInsertTrackPointsTask(
        cancellationToken: CancellationToken,
        pointsEntityList: List<PointEntity>
    ) =
        InsertLocationIntoDbTask().insertLocation(pointsEntityList, cancellationToken)

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
        UpdateTackIntoDbTask().replaceTrackIntoDb(track, cancellationToken)

    fun getUpdateIdTrackTask(id: Long, beginAt: Long, cancellationToken: CancellationToken) =
        UpdateTrackIdTask().updateTrackId(cancellationToken, id, beginAt)

    fun getDeleteTrackFromDbTask(cancellationToken: CancellationToken, condition: String) =
        RemoveTrackFromDbTask().removeTrackFromDb(cancellationToken, condition)

    fun getPointsFromServerTask(
        cancellationToken: CancellationToken,
        pointsRequest: PointsRequest
    ) = GetPointsFromServer().getPoints(cancellationToken, pointsRequest)

    fun getPointsFromDbTask(cancellationToken: CancellationToken, beginAt: Long) =
        GetPointFromDbTask().getPointsFromDb(beginAt, cancellationToken)

    fun getTracksFromDb(cancellationToken: CancellationToken) =
        GetTracksFromDb().getTracksFromDb(cancellationToken)

    fun getUnsentTracks(cancellationToken: CancellationToken) =
        GetUnsentTracksTask().getUnsentTracks(cancellationToken)

    fun getUpdatePointsIntoDbTask(cancellationToken: CancellationToken, pointEntity: PointEntity) =
        UpdatePointIntoDbTask().updatePointIntoDb(cancellationToken, pointEntity)
}