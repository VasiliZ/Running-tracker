package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class SelectDataBuilder(private val tableNames: List<String>) {

    companion object {
        private const val SELECT = "SELECT"
        private const val FROM = "FROM"
        private const val SEPARATOR = ","
    }

    private val selectedFields = mutableListOf<String>()

    fun fieldFromSelect(field: String): SelectDataBuilder {
        selectedFields.add(field)
        return this
    }

    fun build(db: SQLiteDatabase): Cursor {
        return db.rawQuery(
            "$SELECT ${
                selectedFields.joinToString(separator = SEPARATOR) { it }
            } $FROM ${tableNames.joinToString(separator = SEPARATOR) { it }}", null
        )
    }

}