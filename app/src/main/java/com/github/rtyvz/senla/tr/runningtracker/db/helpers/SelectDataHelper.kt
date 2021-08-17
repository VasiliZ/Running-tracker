package com.github.rtyvz.senla.tr.runningtracker.db.helpers

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class SelectDataHelper(private val tableNames: List<String>) {

    companion object {
        private const val SELECT_KEYWORD = "SELECT"
        private const val FROM_KEYWORD = "FROM"
        private const val SEPARATOR = ","
        private const val WHERE_KEYWORD = " WHERE "
        private const val EMPTY_STRING = ""
        private const val ORDER_BY = " ORDER BY "
        private const val DESC = " DESC "
    }

    private val selectedFields = mutableListOf<String>()
    private var where: String = EMPTY_STRING
    private var orderByCondition: String = EMPTY_STRING

    fun fieldFromSelect(field: String): SelectDataHelper {
        selectedFields.add(field)
        return this
    }

    fun orderByDesc(condition: String): SelectDataHelper {
        orderByCondition = condition
        return this
    }

    fun where(whereCondition: String): SelectDataHelper {
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
                    } + when (orderByCondition) {
                EMPTY_STRING -> EMPTY_STRING
                else -> {
                    "$ORDER_BY $orderByCondition $DESC"
                }

            },
            null
        )
    }
}