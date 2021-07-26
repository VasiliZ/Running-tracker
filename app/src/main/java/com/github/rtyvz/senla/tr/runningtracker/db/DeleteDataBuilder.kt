package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.sqlite.SQLiteDatabase

class DeleteDataBuilder(private val tableName: String) {
    private var conditionForDelete: String? = null

    companion object {
        private const val DELETE_FROM = "DELETE FROM "
        private const val WHERE = " WHERE "
    }

    fun where(whereCondition: String): DeleteDataBuilder {
        conditionForDelete = whereCondition
        return this
    }

    fun build(db: SQLiteDatabase) {
        if (conditionForDelete != null) {
            db.execSQL("$DELETE_FROM $tableName $WHERE $conditionForDelete")
        } else {
            throw IllegalArgumentException("Where condition must be placed")
        }
    }
}