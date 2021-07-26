package com.github.rtyvz.senla.tr.runningtracker.db

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity

object DBHelper {
    private const val BEGINS_AT_CONDITION = "beginAt = "
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

    fun selectTracksFromDb() {
        //todo select tracks here
    }
}