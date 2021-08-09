package com.github.rtyvz.senla.tr.runningtracker.providers

import bolts.CancellationToken
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Point
import com.github.rtyvz.senla.tr.runningtracker.entity.network.PointsRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.TracksRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
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
        GetTracksFromNetworkTask().fetchTracks(tracksRequest, cancellationToken)

    fun getInsertTracksIntoDbkTask(
        cancellationToken: CancellationToken,
        listTrack: List<TrackEntity>
    ) =
        InsertTrackIntoDbTask().insertTracksIntiDb(listTrack, cancellationToken)

    fun getInsertTrackPointsTask(
        cancellationToken: CancellationToken,
        pointsList: List<PointEntity>
    ) =
        InsertPointsIntoDbTask().insertTrackPoints(pointsList, cancellationToken)

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

    fun getInsertTrackTask(track: TrackEntity) =
        InsertTackIntoDbTask().replaceTrackIntoDb(track)

    fun getInsertPointTask(point: PointEntity) =
        InsertPointIntoDbTask().insertPoint(point)

    fun getUpdateIdTrackTask(id: Long, beginAt: Long, cancellationToken: CancellationToken) =
        UpdateTrackIdTask().updateTrackId(cancellationToken, id, beginAt)

    fun getDeleteTrackFromDbTask(condition: String) =
        RemoveTrackFromDbTask().removeTrackFromDb(condition)

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

    fun getUpdateTrackTask(track: TrackEntity, cancellationToken: CancellationToken) =
        UpdateTrackTask().updateTrack(track, cancellationToken)

    fun getClearUserDataTask() =
        ClearUserDataTask().clearUserData()

    fun getInsertAlarmTask(alarmEntity: AlarmEntity) =
        SaveNotificationToDbTask().saveNotificationToDb(alarmEntity)

    fun getSelectNotificationsTask() = SelectNotificationsTask().getNotifications()
    fun getUpdateNotificationTask(alarmEntity: AlarmEntity) =
        UpdateNotificationTask().updateNotification(alarmEntity)

    fun getDeleteNotificationByIdTask(alarmId: Int) =
        DeleteNotificationByIdTask().deleteNotificationById(alarmId)
}