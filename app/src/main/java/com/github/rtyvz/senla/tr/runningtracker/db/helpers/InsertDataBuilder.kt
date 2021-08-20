package com.github.rtyvz.senla.tr.runningtracker.db.helpers

import android.database.sqlite.SQLiteDatabase

class InsertDataBuilder(private val tableName: String) {

    companion object {
        private const val SEPARATOR = ","
        private const val OPEN_BRACKET = "("
        private const val CLOSE_BRACKET = ")"
        private const val VALUES = "VALUES"
        private const val QUESTION_MARK = "?"
        private const val NEXT_STATEMENT_INDEX = 1
    }

    private val mapWithData = mutableMapOf<String, Any>()

    fun setFieldsWithData(fieldName: String, data: Any): InsertDataBuilder {
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
                //statement index starts with 1
                is String -> statement?.bindString(index + NEXT_STATEMENT_INDEX, data.toString())
                is Int -> statement?.bindLong(index + NEXT_STATEMENT_INDEX, data.toLong())
                is Long -> statement?.bindLong(index + NEXT_STATEMENT_INDEX, data.toLong())
                is Double -> statement?.bindDouble(index + NEXT_STATEMENT_INDEX, data.toDouble())
                else -> error("wrong type of data")
            }
        }
        statement?.executeInsert()
    }
}