package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.sqlite.SQLiteDatabase

class UpdateTableBuilder(private val tableName: String) {
    companion object {
        private const val UPDATE = "UPDATE "
        private const val SET = "SET "
        private const val SEPARATOR = ","
        private const val QUESTION_MARK = "?"
        private const val WHERE = " WHERE "
    }

    private val fieldsAndDataMap = mutableMapOf<String, Any>()
    private var whereCondition: String? = null

    fun setFieldsWithData(fieldName: String, data: Any): UpdateTableBuilder {
        fieldsAndDataMap[fieldName] = data
        return this
    }

    fun whereCondition(condition: String): UpdateTableBuilder {
        whereCondition = condition
        return this
    }

    fun build(db: SQLiteDatabase) {
        val statement = db.compileStatement("$UPDATE $tableName $SET ${
            fieldsAndDataMap.entries.joinToString(separator = SEPARATOR) {
                "${it.key} = $QUESTION_MARK"
            }
        } $WHERE $whereCondition")

        fieldsAndDataMap.values.forEachIndexed { index, mutableEntry ->
            when (mutableEntry) {
                is String -> statement?.bindString(index + 1, mutableEntry.toString())
                is Int -> statement?.bindLong(index + 1, mutableEntry.toLong())
                is Long -> statement?.bindLong(index + 1, mutableEntry.toLong())
                is Double -> statement?.bindDouble(index + 1, mutableEntry.toDouble())
                is Float -> statement?.bindDouble(index + 1, mutableEntry.toDouble())
                else -> statement?.bindNull(index + 1)
            }
        }
        statement?.executeInsert()
    }
}