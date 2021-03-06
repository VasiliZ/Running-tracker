package com.github.rtyvz.senla.tr.runningtracker.db

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DeleteDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.InsertDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.SelectDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.UpdateDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.map

object TrackQueryObject {
    private const val BEGINS_AT_CONDITION = "beginAt "
    private const val SELECT_ALL = "*"
    private const val UNSENT_TRACKS_FLAG = 0
    private const val SENT_TRACK_FLAG = 1

    fun insertTracksIntoTable(tracksList: List<TrackEntity>) {
        tracksList.forEach { trackEntity ->
            InsertDataBuilder(AppDb.TRACK_TABLE_NAME)
                .setFieldsWithData(AppDb.BEGIN_AT_FIELD_NAME, trackEntity.beginsAt)
                .setFieldsWithData(AppDb.TIME_FIELD_NAME, trackEntity.time)
                .setFieldsWithData(AppDb.DISTANCE_FIELD_NAME, trackEntity.distance)
                .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, trackEntity.id)
                .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, trackEntity.isSent)
                .build(App.db, true)
        }
    }

    fun updateTrackIdFromBeginsAt(id: Long, beginsAt: Long) {
        UpdateDataBuilder(AppDb.TRACK_TABLE_NAME)
            .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, id)
            .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, SENT_TRACK_FLAG)
            .whereCondition("$BEGINS_AT_CONDITION = $beginsAt")
            .build(App.db)
    }

    fun deleteTrackFromDb(conditionForDelete: String) {
        DeleteDataBuilder(AppDb.TRACK_TABLE_NAME)
            .where("${AppDb.BEGIN_AT_FIELD_NAME} = $conditionForDelete")
            .build(App.db)
    }

    fun updateTrack(trackEntity: TrackEntity) {
        UpdateDataBuilder(AppDb.TRACK_TABLE_NAME)
            .setFieldsWithData(AppDb.TIME_FIELD_NAME, trackEntity.time)
            .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, trackEntity.id)
            .setFieldsWithData(AppDb.DISTANCE_FIELD_NAME, trackEntity.distance)
            .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, trackEntity.isSent)
            .whereCondition("${AppDb.BEGIN_AT_FIELD_NAME} = ${trackEntity.beginsAt}")
            .build(App.db)
    }

    fun getTracksFromDb() = SelectDataBuilder(listOf(AppDb.TRACK_TABLE_NAME))
        .fieldFromSelect(SELECT_ALL)
        .orderByDesc(AppDb.BEGIN_AT_FIELD_NAME)
        .build(App.db)?.map { cursor ->
            TrackEntity(
                id = cursor.getLong(cursor.getColumnIndex(AppDb.REMOTE_ID_FIELD_NAME)),
                beginsAt = cursor.getLong(cursor.getColumnIndex(AppDb.BEGIN_AT_FIELD_NAME)),
                time = cursor.getLong(cursor.getColumnIndex(AppDb.TIME_FIELD_NAME)),
                distance = cursor.getInt(cursor.getColumnIndex(AppDb.DISTANCE_FIELD_NAME))
            )
        }

    fun getUnsentTracksFromDb() = SelectDataBuilder(listOf(AppDb.TRACK_TABLE_NAME))
        .fieldFromSelect(SELECT_ALL)
        .where("${AppDb.IS_SENT_FIELD_NAME} = $UNSENT_TRACKS_FLAG")
        .build(App.db)?.map { trackCursor ->
            TrackEntity(
                id = trackCursor.getLong(trackCursor.getColumnIndex(AppDb.REMOTE_ID_FIELD_NAME)),
                beginsAt = trackCursor.getLong(trackCursor.getColumnIndex(AppDb.BEGIN_AT_FIELD_NAME)),
                time = trackCursor.getLong(trackCursor.getColumnIndex(AppDb.TIME_FIELD_NAME)),
                distance = trackCursor.getInt(trackCursor.getColumnIndex(AppDb.DISTANCE_FIELD_NAME)),
                listPoints = PointsQueryObject.getTrackPointsFromDB(
                    trackCursor.getLong(
                        trackCursor.getColumnIndex(
                            AppDb.BEGIN_AT_FIELD_NAME
                        )
                    )
                ) ?: emptyList()
            )
        }

    fun deleteDataFromTrackTable() {
        DeleteDataBuilder(AppDb.TRACK_TABLE_NAME)
            .build(App.db)
    }
}