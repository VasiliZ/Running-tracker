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

    fun getReplaceTrackIntoDbkTask(
        cancellationToken: CancellationToken,
        listTrack: List<TrackEntity>
    ) =
        ReplaceTrackIntoDbTask().replaceTracksIntiDb(listTrack, cancellationToken)

    fun getReplaceTrackPointsTask(
        cancellationToken: CancellationToken,
        pointsList: List<PointEntity>
    ) =
        ReplaceOrInsertPointLocation().replaceTrackPoints(pointsList, cancellationToken)

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

    fun getInsertTrackTask(track: TrackEntity, cancellationToken: CancellationToken) =
        InsertTackIntoDbTask().replaceTrackIntoDb(track, cancellationToken)

    fun getInsertPointTask(point: PointEntity, cancellationToken: CancellationToken) =
        InsertPointIntoDbTask().insertPoint(cancellationToken, point)

    fun getUpdateIdTrackTask(id: Long, beginAt: Long, cancellationToken: CancellationToken) =
        UpdateTrackIdTask().updateTrackId(cancellationToken, id, beginAt)

    fun getDeleteTrackFromDbTask(cancellationToken: CancellationToken, condition: String) =
        RemoveTrackFromDbTask().removeTrackFromDb(cancellationToken, condition)

    fun getPointsFromServerTask(
        cancellationToken: CancellationToken,
        pointsRequest: PointsRequest
    ) = GetPointsFromServerTask().getPoints(cancellationToken, pointsRequest)

    fun getPointsFromDbTask(cancellationToken: CancellationToken, beginAt: Long) =
        GetPointFromDbTask().getPointsFromDb(beginAt, cancellationToken)

    fun getTracksFromDb(cancellationToken: CancellationToken) =
        GetTracksFromDbTask().getTracksFromDb(cancellationToken)

    fun getUnsentTracks(cancellationToken: CancellationToken) =
        GetUnsentTracksTask().getUnsentTracks(cancellationToken)

    fun getUpdatePointsIntoDbTask(cancellationToken: CancellationToken, points: List<PointEntity>) =
        ReplacePointIntoDbTask().updatePointIntoDb(cancellationToken, points)
}