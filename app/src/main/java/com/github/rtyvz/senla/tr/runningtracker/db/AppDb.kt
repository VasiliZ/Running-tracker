package com.github.rtyvz.senla.tr.runningtracker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDb(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "running_tracker.db"
        const val TRACK_TABLE_NAME = "tracks"
        const val POINTS_TABLE_NAME = "points"
        const val REMOTE_ID_FIELD_NAME = "remote_id"
        private const val PRIMARY_FIELD_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT"
        private const val INT_NOT_NULL_TYPE = "INTEGER NOT NULL"
        private const val REAL_NOT_NULL_TYPE = "REAL NOT NULL"
        private const val INT_DEFAULT_ZERO_TYPE = "INTEGER DEFAULT 0"
        const val BEGIN_AT_FIELD_NAME = "beginAt"
        const val TIME_FIELD_NAME = "time"
        const val DISTANCE_FIELD_NAME = "distance"
        const val LNG_FIELD_NAME = "lng"
        const val ID_FIELD_NAME = "id"
        const val LAT_FIELD_NAME = "lat"
        const val IS_SENT_FIELD_NAME = "isSent"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        CreateTableBuilder(TRACK_TABLE_NAME)
            .setTableField(BEGIN_AT_FIELD_NAME, PRIMARY_FIELD_TYPE)
            .setTableField(TIME_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(REMOTE_ID_FIELD_NAME, INT_DEFAULT_ZERO_TYPE)
            .setTableField(DISTANCE_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(IS_SENT_FIELD_NAME, INT_NOT_NULL_TYPE)
            .build(db)

        CreateTableBuilder(POINTS_TABLE_NAME)
            .setTableField(ID_FIELD_NAME, PRIMARY_FIELD_TYPE)
            .setTableField(BEGIN_AT_FIELD_NAME, INT_NOT_NULL_TYPE)
            .setTableField(LNG_FIELD_NAME, REAL_NOT_NULL_TYPE)
            .setTableField(LAT_FIELD_NAME, REAL_NOT_NULL_TYPE)
            .setUniqueFields(listOf(BEGIN_AT_FIELD_NAME, LNG_FIELD_NAME, LAT_FIELD_NAME))
            .build(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}