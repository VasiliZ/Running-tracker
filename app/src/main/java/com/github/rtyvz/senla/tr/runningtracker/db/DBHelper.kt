package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.Cursor
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

object DBHelper {
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
                .build(App.db)
        }
    }

    fun insertPointsIntoTheTable(pointsList: List<PointEntity>) {
        pointsList.forEach { pointEntity ->
            InsertDataTableBuilder(AppDb.POINTS_TABLE_NAME)
                .setFieldsWithDataForReplace(AppDb.BEGIN_AT_FIELD_NAME, pointEntity.beginAt)
                .setFieldsWithDataForReplace(AppDb.LNG_FIELD_NAME, pointEntity.lng)
                .setFieldsWithDataForReplace(AppDb.LAT_FIELD_NAME, pointEntity.lat)
                .build(App.db, true)
        }
    }

    fun insertPoint(point: PointEntity) {
        InsertDataBuilder(AppDb.POINTS_TABLE_NAME)
            .setFieldsWithData(AppDb.BEGIN_AT_FIELD_NAME, point.beginAt)
            .setFieldsWithData(AppDb.LNG_FIELD_NAME, point.lng)
            .setFieldsWithData(AppDb.LAT_FIELD_NAME, point.lat)
            .build(App.db)
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
        return SelectDataBuilder(listOf(AppDb.POINTS_TABLE_NAME))
            .fieldFromSelect("${AppDb.POINTS_TABLE_NAME}.*")
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
        DeleteDataBuilder(AppDb.TRACK_TABLE_NAME)
            .build(App.db)
    }

    fun deleteDataFromPointTable() {
        DeleteDataBuilder(AppDb.POINTS_TABLE_NAME)
            .build(App.db)
    }
}