package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.Cursor
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.ALARM_ID_FIELD_NAME
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.ALARM_TABLE_NAME
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.DAY_FIELD_NAME
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.HOUR_FIELD_NAME
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.IS_ENABLED_NOTIFICATION
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.MINUTE_FIELD_NAME
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.OLD_ID_FIELD_NAME
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.POINTS_TABLE_NAME
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb.Companion.TITLE_FIELD_NAME
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

object DBHelper {
    private const val BEGINS_AT_CONDITION = "beginAt "
    private const val SELECT_ALL = "*"
    private const val UNSENT_TRACKS_FLAG = 0
    private const val SENT_TRACK_FLAG = 1

    fun insertTracksIntoTable(tracksList: List<TrackEntity>) {
        tracksList.forEach { trackEntity ->
            InsertDataTableHelper(AppDb.TRACK_TABLE_NAME)
                .setFieldsWithDataForReplace(AppDb.BEGIN_AT_FIELD_NAME, trackEntity.beginsAt)
                .setFieldsWithDataForReplace(AppDb.TIME_FIELD_NAME, trackEntity.time)
                .setFieldsWithDataForReplace(AppDb.DISTANCE_FIELD_NAME, trackEntity.distance)
                .setFieldsWithDataForReplace(AppDb.REMOTE_ID_FIELD_NAME, trackEntity.id)
                .setFieldsWithDataForReplace(AppDb.IS_SENT_FIELD_NAME, trackEntity.isSent)
                .build(App.db, true)
        }
    }

    fun insertPointsIntoTheTable(pointsList: List<PointEntity>) {
        pointsList.forEach { pointEntity ->
            InsertDataTableHelper(POINTS_TABLE_NAME)
                .setFieldsWithDataForReplace(AppDb.BEGIN_AT_FIELD_NAME, pointEntity.beginAt)
                .setFieldsWithDataForReplace(AppDb.LNG_FIELD_NAME, pointEntity.lng)
                .setFieldsWithDataForReplace(AppDb.LAT_FIELD_NAME, pointEntity.lat)
                .build(App.db, true)
        }
    }

    fun insertPoint(point: PointEntity) {
        InsertDataHelper(POINTS_TABLE_NAME)
            .setFieldsWithData(AppDb.BEGIN_AT_FIELD_NAME, point.beginAt)
            .setFieldsWithData(AppDb.LNG_FIELD_NAME, point.lng)
            .setFieldsWithData(AppDb.LAT_FIELD_NAME, point.lat)
            .build(App.db)
    }

    fun updateTrackIdFromBeginsAt(id: Long, beginsAt: Long) {
        UpdateTableHelper(AppDb.TRACK_TABLE_NAME)
            .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, id)
            .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, SENT_TRACK_FLAG)
            .whereCondition("$BEGINS_AT_CONDITION = $beginsAt")
            .build(App.db)
    }

    fun deleteTrackFromDb(conditionForDelete: String) {
        DeleteTableDataHelper(AppDb.TRACK_TABLE_NAME)
            .where("${AppDb.BEGIN_AT_FIELD_NAME} = $conditionForDelete")
            .build(App.db)
    }

    fun updateTrack(trackEntity: TrackEntity) {
        UpdateTableHelper(AppDb.TRACK_TABLE_NAME)
            .setFieldsWithData(AppDb.TIME_FIELD_NAME, trackEntity.time)
            .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, trackEntity.id)
            .setFieldsWithData(AppDb.DISTANCE_FIELD_NAME, trackEntity.distance)
            .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, trackEntity.isSent)
            .whereCondition("${AppDb.BEGIN_AT_FIELD_NAME} = ${trackEntity.beginsAt}")
            .build(App.db)
    }

    fun getTracksFromDb(): List<TrackEntity> {
        val listTracks = mutableListOf<TrackEntity>()
        val cursor = SelectDataHelper(listOf(AppDb.TRACK_TABLE_NAME))
            .fieldFromSelect(SELECT_ALL)
            .build(App.db)
        cursor.use {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    listTracks.add(
                        TrackEntity(
                            id = cursor.getLong(cursor.getColumnIndex(AppDb.REMOTE_ID_FIELD_NAME)),
                            beginsAt = cursor.getLong(cursor.getColumnIndex(AppDb.BEGIN_AT_FIELD_NAME)),
                            time = cursor.getLong(cursor.getColumnIndex(AppDb.TIME_FIELD_NAME)),
                            distance = cursor.getInt(cursor.getColumnIndex(AppDb.DISTANCE_FIELD_NAME))
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return listTracks
    }

    fun getUnsentTracksFromDb(): List<TrackEntity> {
        val listTracks = mutableListOf<TrackEntity>()
        val trackCursor = SelectDataHelper(listOf(AppDb.TRACK_TABLE_NAME))
            .fieldFromSelect(SELECT_ALL)
            .where("${AppDb.IS_SENT_FIELD_NAME} = $UNSENT_TRACKS_FLAG")
            .build(App.db)

        trackCursor.use {
            if (trackCursor != null && trackCursor.moveToFirst()) {
                do {
                    listTracks.add(
                        TrackEntity(
                            id = trackCursor.getLong(trackCursor.getColumnIndex(AppDb.REMOTE_ID_FIELD_NAME)),
                            beginsAt = trackCursor.getLong(trackCursor.getColumnIndex(AppDb.BEGIN_AT_FIELD_NAME)),
                            time = trackCursor.getLong(trackCursor.getColumnIndex(AppDb.TIME_FIELD_NAME)),
                            distance = trackCursor.getInt(trackCursor.getColumnIndex(AppDb.DISTANCE_FIELD_NAME)),
                            listPoints = getTrackPointsFromDB(
                                trackCursor.getLong(
                                    trackCursor.getColumnIndex(
                                        AppDb.BEGIN_AT_FIELD_NAME
                                    )
                                )
                            )
                        )
                    )
                } while (trackCursor.moveToNext())
            }
        }
        return listTracks
    }

    private fun selectPointsFromDb(beginsAt: Long): Cursor? {
        return SelectDataHelper(listOf(POINTS_TABLE_NAME))
            .fieldFromSelect("${POINTS_TABLE_NAME}.*")
            .orderByAsc(AppDb.ID_FIELD_NAME)
            .where("${AppDb.BEGIN_AT_FIELD_NAME} = $beginsAt")
            .build(App.db)
    }

    fun getTrackPointsFromDB(beginsAt: Long): List<PointEntity> {
        val cursor = selectPointsFromDb(beginsAt)
        val listPoints = mutableListOf<PointEntity>()
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    listPoints.add(
                        PointEntity(
                            lat = it.getDouble(it.getColumnIndex(AppDb.LAT_FIELD_NAME)),
                            lng = it.getDouble(it.getColumnIndex(AppDb.LNG_FIELD_NAME)),
                            beginAt = it.getLong(it.getColumnIndex(AppDb.BEGIN_AT_FIELD_NAME))
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return listPoints
    }

    fun deleteDataFromTrackTable() {
        DeleteTableDataHelper(AppDb.TRACK_TABLE_NAME)
            .build(App.db)
    }

    fun deleteDataFromPointTable() {
        DeleteTableDataHelper(POINTS_TABLE_NAME)
            .build(App.db)
    }

    fun deleteDataFromAlarmsTable() {
        DeleteTableDataHelper(ALARM_TABLE_NAME)
            .build(App.db)
    }

    fun insertNotificationToDb(alarmEntity: AlarmEntity) {
        InsertDataHelper(ALARM_TABLE_NAME)
            .setFieldsWithData(ALARM_ID_FIELD_NAME, alarmEntity.alarmId)
            .setFieldsWithData(HOUR_FIELD_NAME, alarmEntity.hour)
            .setFieldsWithData(MINUTE_FIELD_NAME, alarmEntity.minute)
            .setFieldsWithData(TITLE_FIELD_NAME, alarmEntity.title)
            .setFieldsWithData(DAY_FIELD_NAME, alarmEntity.day)
            .setFieldsWithData(OLD_ID_FIELD_NAME, alarmEntity.oldId)
            .setFieldsWithData(IS_ENABLED_NOTIFICATION, alarmEntity.isEnabled)
            .build(App.db)
    }

    fun getNotifications(): List<AlarmEntity> {
        val listNotification = mutableListOf<AlarmEntity>()
        val cursor = SelectDataHelper(listOf(ALARM_TABLE_NAME))
            .fieldFromSelect("${ALARM_TABLE_NAME}.*")
            .orderByAsc(HOUR_FIELD_NAME)
            .build(App.db)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    listNotification.add(
                        AlarmEntity(
                            alarmId = it.getInt(it.getColumnIndex(ALARM_ID_FIELD_NAME)),
                            hour = it.getInt(it.getColumnIndex(HOUR_FIELD_NAME)),
                            minute = it.getInt(it.getColumnIndex(MINUTE_FIELD_NAME)),
                            title = it.getString(it.getColumnIndex(TITLE_FIELD_NAME)),
                            day = it.getLong(it.getColumnIndex(DAY_FIELD_NAME)),
                            isEnabled = it.getInt(it.getColumnIndex(IS_ENABLED_NOTIFICATION)),
                            oldId = it.getInt(it.getColumnIndex(OLD_ID_FIELD_NAME))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return listNotification
    }

    fun updateNotification(alarmEntity: AlarmEntity) {
        UpdateTableHelper(ALARM_TABLE_NAME)
            .setFieldsWithData(ALARM_ID_FIELD_NAME, alarmEntity.alarmId)
            .setFieldsWithData(HOUR_FIELD_NAME, alarmEntity.hour)
            .setFieldsWithData(MINUTE_FIELD_NAME, alarmEntity.minute)
            .setFieldsWithData(TITLE_FIELD_NAME, alarmEntity.title)
            .setFieldsWithData(DAY_FIELD_NAME, alarmEntity.day)
            .setFieldsWithData(IS_ENABLED_NOTIFICATION, alarmEntity.isEnabled)
            .setFieldsWithData(OLD_ID_FIELD_NAME, alarmEntity.oldId)
            .whereCondition("$ALARM_ID_FIELD_NAME = ${alarmEntity.oldId}")
            .build(App.db)
    }

    fun deleteNotificationById(alarmId: Int) {
        DeleteTableDataHelper(ALARM_TABLE_NAME)
            .where("$ALARM_ID_FIELD_NAME = $alarmId")
            .build(App.db)
    }

    fun deleteTrackPoints(startRunningTime: Long) {
        DeleteTableDataHelper(POINTS_TABLE_NAME)
            .where("${AppDb.BEGIN_AT_FIELD_NAME} = $startRunningTime")
            .build(App.db)
    }

    fun updateNotificationStateById(alarmId: Int, stateFlag: Int) {
        UpdateTableHelper(ALARM_TABLE_NAME)
            .setFieldsWithData(IS_ENABLED_NOTIFICATION, stateFlag)
            .whereCondition("$ALARM_ID_FIELD_NAME = $alarmId")
            .build(App.db)
    }
}