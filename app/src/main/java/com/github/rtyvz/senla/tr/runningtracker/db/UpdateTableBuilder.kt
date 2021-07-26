package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.sqlite.SQLiteDatabase

class UpdateTableBuilder(private val tableName: String) {
    companion object {
        private const val UPDATE = "UPDATE "
        private const val SET = "SET "
        private const val SEPARATOR = ","
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
        db.execSQL(
            "$UPDATE $tableName $SET ${
                fieldsAndDataMap.entries.joinToString(separator = SEPARATOR) {
                    "${it.key} = ${it.value}"
                }
            } $WHERE $whereCondition"
        )
    }
}