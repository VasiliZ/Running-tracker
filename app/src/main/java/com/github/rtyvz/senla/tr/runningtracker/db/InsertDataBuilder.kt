package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class InsertDataBuilder(private val tableName: String) {

    companion object {
        private const val QUESTION_MARK = "?"
        private const val SEPARATOR = ","
        private const val INSERT_STATEMENT = "INSERT INTO "
        private const val OPEN_BRACKET = "("
        private const val CLOSE_BRACKET = ")"
        private const val VALUES = " VALUES "
    }

    private val fieldsWithDataMap = mutableMapOf<String, Any>()

    fun setFieldsWithData(fieldName: String, data: Any): InsertDataBuilder {
        fieldsWithDataMap[fieldName] = data
        return this
    }

    fun build(db: SQLiteDatabase) {
        val questionMarks = StringBuilder()
        fieldsWithDataMap.forEach { _ ->
            questionMarks.append(QUESTION_MARK)
            questionMarks.append(SEPARATOR)
        }
        //delete last separator
        questionMarks.deleteAt(questionMarks.lastIndex)
        val statement = db.compileStatement(
            "$INSERT_STATEMENT $tableName ${
                fieldsWithDataMap.entries.joinToString(
                    prefix = OPEN_BRACKET,
                    postfix = CLOSE_BRACKET,
                    separator = SEPARATOR
                ) {
                    it.key
                }
            } $VALUES $OPEN_BRACKET $questionMarks $CLOSE_BRACKET"
        )
        fieldsWithDataMap.values.forEachIndexed { index, mutableEntry ->
            when (mutableEntry) {
                //index +1 because statement starts with 1 but index in loop starts with 0
                is String -> statement?.bindString(index + 1, mutableEntry.toString())
                is Int -> statement?.bindLong(index + 1, mutableEntry.toLong())
                is Long -> statement?.bindLong(index + 1, mutableEntry.toLong())
                is Double -> statement?.bindDouble(index + 1, mutableEntry.toDouble())
                else -> statement?.bindNull(index + 1)
            }
        }
        try {
            statement?.executeInsert()
        } catch (e: SQLiteConstraintException) {
            Log.e("exception", "we have the same track")
        }
    }
}