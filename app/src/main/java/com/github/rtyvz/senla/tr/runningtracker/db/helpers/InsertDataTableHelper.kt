package com.github.rtyvz.senla.tr.runningtracker.db.helpers

import android.database.sqlite.SQLiteDatabase

class InsertDataTableHelper(private val tableName: String) {

    companion object {
        private const val SEPARATOR = ","
        private const val OPEN_BRACKET = "("
        private const val CLOSE_BRACKET = ")"
        private const val VALUES = "VALUES"
        private const val QUESTION_MARK = "?"
    }

    private val mapWithData = mutableMapOf<String, Any>()

    fun setFieldsWithDataForReplace(fieldName: String, data: Any): InsertDataTableHelper {
        mapWithData[fieldName] = data
        return this
    }

    fun build(db: SQLiteDatabase, isIgnore: Boolean = false) {
        val statement = db.compileStatement(
            "INSERT ${
                when (isIgnore) {
                    true -> " OR IGNORE "
                    else -> ""
                }
            }INTO $tableName ${
                mapWithData.keys.joinToString(
                    separator = SEPARATOR,
                    prefix = OPEN_BRACKET,
                    postfix = CLOSE_BRACKET
                ) { it }
            } $VALUES ${
                mapWithData.entries.joinToString(
                    separator = SEPARATOR,
                    prefix = OPEN_BRACKET,
                    postfix = CLOSE_BRACKET
                ) {
                    QUESTION_MARK
                }
            } "
        )
        mapWithData.values.forEachIndexed { index, data ->
            when (data) {
                is String -> statement?.bindString(index + 1, data.toString())
                is Int -> statement?.bindLong(index + 1, data.toLong())
                is Long -> statement?.bindLong(index + 1, data.toLong())
                is Double -> statement?.bindDouble(index + 1, data.toDouble())
                else -> statement?.bindNull(index + 1)
            }
        }
        statement?.executeInsert()
    }
}