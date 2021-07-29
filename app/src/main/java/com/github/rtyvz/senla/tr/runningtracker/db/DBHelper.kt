package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.Cursor
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

object DBHelper {
    private const val BEGINS_AT_CONDITION = "beginAt = "
    private const val SELECT_ALL = "*"

    fun insertTracksIntoTable(tracks: List<TrackEntity>) {
        tracks.forEach {
            InsertDataBuilder(AppDb.TRACK_TABLE_NAME)
                .setFieldsWithData(AppDb.BEGIN_AT_FIELD_NAME, it.beginsAt)
                .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, it.id)
                .setFieldsWithData(AppDb.DISTANCE_FIELD_NAME, it.distance)
                .setFieldsWithData(AppDb.TIME_FIELD_NAME, it.time)
                .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, it.isSent)
                .build(App.db)
        }
    }

    fun insertPointsIntoTable(points: List<PointEntity>) {
        points.forEach {
            InsertDataBuilder(AppDb.POINTS_TABLE_NAME)
                .setFieldsWithData(AppDb.LAT_FIELD_NAME, it.lat)
                .setFieldsWithData(AppDb.LNG_FIELD_NAME, it.lng)
                .setFieldsWithData(AppDb.BEGIN_AT_FIELD_NAME, it.beginAt)
                .build(App.db)
        }
    }

    fun updateTackIntoTable(trackEntity: TrackEntity) {
        UpdateTableBuilder(AppDb.TRACK_TABLE_NAME)
            .setFieldsWithData(AppDb.TIME_FIELD_NAME, trackEntity.time)
            .setFieldsWithData(AppDb.DISTANCE_FIELD_NAME, trackEntity.distance)
            .setFieldsWithData(AppDb.REMOTE_ID_FIELD_NAME, trackEntity.id)
            .setFieldsWithData(AppDb.IS_SENT_FIELD_NAME, trackEntity.isSent)
            .whereCondition("$BEGINS_AT_CONDITION ${trackEntity.beginsAt}")
            .build(App.db)
    }

    fun deleteTrackFromDb(conditionForDelete: String) {
        DeleteDataBuilder(AppDb.TRACK_TABLE_NAME)
            .where("${AppDb.BEGIN_AT_FIELD_NAME} = $conditionForDelete")
            .build(App.db)
    }

    fun getTracksFromDb(): List<TrackEntity> {
        val listTracks = mutableListOf<TrackEntity>()
        val cursor = SelectDataBuilder(listOf(AppDb.TRACK_TABLE_NAME))
            .fieldFromSelect(SELECT_ALL)
            .build(App.db)
        try {
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
        } finally {
            cursor?.close()
        }
        return listTracks
    }

    private fun selectPointsFromDb(beginsAt: Long): Cursor? {
        return SelectDataBuilder(listOf(AppDb.POINTS_TABLE_NAME))
            .fieldFromSelect("${AppDb.POINTS_TABLE_NAME}.*")
            .where(beginsAt.toString())
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
                } while (cursor.moveToFirst())
            }
        }
        return listPoints
    }
}