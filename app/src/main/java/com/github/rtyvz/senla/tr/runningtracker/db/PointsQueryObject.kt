package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.Cursor
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DeleteDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.InsertDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.InsertDataTableBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.SelectDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.toList

object PointsQueryObject {
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

    private fun selectPointsFromDb(beginsAt: Long): Cursor? {
        return SelectDataBuilder(listOf(AppDb.POINTS_TABLE_NAME))
            .fieldFromSelect("${AppDb.POINTS_TABLE_NAME}.*")
            .orderByDesc(AppDb.ID_FIELD_NAME)
            .where("${AppDb.BEGIN_AT_FIELD_NAME} = $beginsAt")
            .build(App.db)
    }

    fun getTrackPointsFromDB(beginsAt: Long) =
        selectPointsFromDb(beginsAt)?.use { cursor ->
            cursor.toList {
                PointEntity(
                    lat = it.getDouble(it.getColumnIndex(AppDb.LAT_FIELD_NAME)),
                    lng = it.getDouble(it.getColumnIndex(AppDb.LNG_FIELD_NAME)),
                    beginAt = it.getLong(it.getColumnIndex(AppDb.BEGIN_AT_FIELD_NAME))
                )
            }
        }

    fun deleteDataFromPointTable() {
        DeleteDataBuilder(AppDb.POINTS_TABLE_NAME)
            .build(App.db)
    }

    fun deleteTrackPoints(startRunningTime: Long) {
        DeleteDataBuilder(AppDb.POINTS_TABLE_NAME)
            .where("${AppDb.BEGIN_AT_FIELD_NAME} = $startRunningTime")
            .build(App.db)
    }
}