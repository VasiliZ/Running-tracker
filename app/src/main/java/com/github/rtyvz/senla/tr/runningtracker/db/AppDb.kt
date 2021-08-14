package com.github.rtyvz.senla.tr.runningtracker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.CreateTableHelper

class AppDb(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "running_tracker.db"
        const val TRACK_TABLE_NAME = "tracks"
        const val POINTS_TABLE_NAME = "points"
        const val ALARM_TABLE_NAME = "alarms"
        const val REMOTE_ID_FIELD_NAME = "remote_id"
        private const val PRIMARY_FIELD_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT"
        private const val INT_NOT_NULL_TYPE = "INTEGER NOT NULL"
        private const val REAL_NOT_NULL_TYPE = "REAL NOT NULL"
        private const val STRING_NOT_NULL_TYPE = "STRING NOT NULL"
        private const val INT_DEFAULT_ZERO_TYPE = "INTEGER DEFAULT 0"
        const val BEGIN_AT_FIELD_NAME = "beginAt"
        const val TIME_FIELD_NAME = "time"
        const val DISTANCE_FIELD_NAME = "distance"
        const val LNG_FIELD_NAME = "lng"
        const val ID_FIELD_NAME = "id"
        const val LAT_FIELD_NAME = "lat"
        const val IS_SENT_FIELD_NAME = "isSent"
        const val ALARM_ID_FIELD_NAME = "alarmId"
        const val HOUR_FIELD_NAME = "hour"
        const val MINUTE_FIELD_NAME = "minute"
        const val TITLE_FIELD_NAME = "title"
        const val DAY_FIELD_NAME = "day"
        const val IS_ENABLED_NOTIFICATION = "isEnabled"
        const val OLD_ID_FIELD_NAME = "oldId"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        CreateTableHelper(TRACK_TABLE_NAME)
            .setTableField(ID_FIELD_NAME, PRIMARY_FIELD_TYPE)
            .setTableField(BEGIN_AT_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(TIME_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(REMOTE_ID_FIELD_NAME, INT_DEFAULT_ZERO_TYPE)
            .setTableField(DISTANCE_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(IS_SENT_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setUniqueFields(listOf(BEGIN_AT_FIELD_NAME))
            .build(db)

        CreateTableHelper(POINTS_TABLE_NAME)
            .setTableField(BEGIN_AT_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(LNG_FIELD_NAME, REAL_NOT_NULL_TYPE)
            .setTableField(LAT_FIELD_NAME, REAL_NOT_NULL_TYPE)
            .setTableField(ID_FIELD_NAME, PRIMARY_FIELD_TYPE)
            .setUniqueFields(listOf(BEGIN_AT_FIELD_NAME, LNG_FIELD_NAME, LAT_FIELD_NAME))
            .build(db)

        CreateTableHelper(ALARM_TABLE_NAME)
            .setTableField(ID_FIELD_NAME, PRIMARY_FIELD_TYPE)
            .setTableField(ALARM_ID_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(HOUR_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(MINUTE_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(TITLE_FIELD_NAME, STRING_NOT_NULL_TYPE)
            .setTableField(DAY_FIELD_NAME, STRING_NOT_NULL_TYPE)
            .setTableField(IS_ENABLED_NOTIFICATION, INT_NOT_NULL_TYPE)
            .setTableField(OLD_ID_FIELD_NAME, INT_NOT_NULL_TYPE)
            .build(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}