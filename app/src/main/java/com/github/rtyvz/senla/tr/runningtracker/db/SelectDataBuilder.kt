package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class SelectDataBuilder(private val tableNames: List<String>) {

    companion object {
        private const val SELECT_KEYWORD = "SELECT"
        private const val FROM_KEYWORD = "FROM"
        private const val SEPARATOR = ","
        private const val WHERE_KEYWORD = " WHERE "
        private const val EMPTY_STRING = ""
    }

    private val selectedFields = mutableListOf<String>()
    private var where: String = EMPTY_STRING

    fun fieldFromSelect(field: String): SelectDataBuilder {
        selectedFields.add(field)
        return this
    }

    fun where(whereCondition: String): SelectDataBuilder {
        where = whereCondition
        return this
    }

    fun build(db: SQLiteDatabase): Cursor? {
        return db.rawQuery(
            "$SELECT_KEYWORD ${
                selectedFields.joinToString(separator = SEPARATOR) { it }
            } $FROM_KEYWORD ${tableNames.joinToString(separator = SEPARATOR) { it }} " +
                    when (where) {
                        EMPTY_STRING -> EMPTY_STRING
                        else -> {
                            "$WHERE_KEYWORD $where"
                        }
                    },
            null
        )
    }
}