package com.github.rtyvz.senla.tr.runningtracker.db

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DeleteDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.InsertDataTableBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.SelectDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.UpdateTableBuilder
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

object TrackQueryObject {
    private const val BEGINS_AT_CONDITION = "beginAt "
    private const val SELECT_ALL = "*"
    private const val UNSENT_TRACKS_FLAG = 0
    private const val SENT_TRACK_FLAG = 1

    fun insertTracksIntoTable(tracksList: List<TrackEntity>) {
        tracksList.forEach { trackEntity ->
            InsertDataTableBuilder(AppDb.TRACK_TABLE_NAME)
                .setFieldsWithDataForReplace(AppDb.BEGIN_AT_FIELD_NAME, trackEntity.beginsAt)
                .setFieldsWithDataForReplace(AppDb.TIME_FIELD_NAME, trackEntity.time)
                .setFieldsWithDataForReplace(AppDb.DISTANCE_FIELD_NAME, trackEntity.distance)
                .setFieldsWithDataForReplace(AppDb.REMOTE_ID_FIELD_NAME, trackEntity.id)
                .setFieldsWithDataForReplace(AppDb.IS_SENT_FIELD_NAME, trackEntity.isSent)
                .build(App.db, true)
        }
    }

    fun updateTrackIdFromBeginsAt(id: Long, beginsAt: Long) {
        UpdateTableBuilder(AppDb.TRACK_TABLE_NAME)
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
        UpdateTableBuilder(AppDb.TRACK_TABLE_NAME)
            .setFieldsWithData(AppDb.TIME_FIELD_NAME, trackEntity.time)
            .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, trackEntity.id)
            .setFieldsWithData(AppDb.DISTANCE_FIELD_NAME, trackEntity.distance)
            .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, trackEntity.isSent)
            .whereCondition("${AppDb.BEGIN_AT_FIELD_NAME} = ${trackEntity.beginsAt}")
            .build(App.db)
    }

    fun getTracksFromDb(): List<TrackEntity> {
        val listTracks = mutableListOf<TrackEntity>()
        val cursor = SelectDataBuilder(listOf(AppDb.TRACK_TABLE_NAME))
            .fieldFromSelect(SELECT_ALL)
            .orderByDesc(AppDb.BEGIN_AT_FIELD_NAME)
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
        val trackCursor = SelectDataBuilder(listOf(AppDb.TRACK_TABLE_NAME))
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
                            listPoints = PointsQueryObject.getTrackPointsFromDB(
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

    fun deleteDataFromTrackTable() {
        DeleteDataBuilder(AppDb.TRACK_TABLE_NAME)
            .build(App.db)
    }
}