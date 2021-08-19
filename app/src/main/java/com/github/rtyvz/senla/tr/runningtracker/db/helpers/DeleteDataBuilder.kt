package com.github.rtyvz.senla.tr.runningtracker.db.helpers

import android.database.sqlite.SQLiteDatabase

class DeleteDataBuilder(private val tableName: String) {

    private var conditionForDelete: String? = null

    companion object {
        private const val DELETE_FROM = "DELETE FROM "
        private const val WHERE = " WHERE "
        private const val EMPTY_STRING = ""
    }

    fun where(whereCondition: String): DeleteDataBuilder {
        //only one condition expected now
        if (conditionForDelete == null) {
            conditionForDelete = whereCondition
        } else {
            error("only one condition expected now")
        }

        return this
    }

    fun build(db: SQLiteDatabase) {
        db.execSQL(
            "$DELETE_FROM $tableName ${
                when (conditionForDelete) {
                    null -> EMPTY_STRING
                    else -> {
                        "$WHERE  $conditionForDelete"
                    }
                }
            }"
        )
    }
}