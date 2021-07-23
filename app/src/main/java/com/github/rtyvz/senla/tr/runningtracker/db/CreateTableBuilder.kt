package com.github.rtyvz.senla.tr.runningtracker.db

import android.database.sqlite.SQLiteDatabase

class CreateTableBuilder(private val tableName: String) {

    companion object {
        private const val CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS"
        private const val OPEN_BRACKET = "("
        private const val CLOSE_BRACKET = ")"
    }

    private val fields = mutableMapOf<String, String>()

    fun setTableField(fieldName: String, fieldType: String): CreateTableBuilder {
        fields[fieldName] = fieldType
        return this
    }

    fun build(db: SQLiteDatabase?) {

        db?.execSQL(
            "$CREATE_STATEMENT $tableName ${
                fields.entries.joinToString(
                    prefix = OPEN_BRACKET,
                    postfix = CLOSE_BRACKET
                ) { "${it.key} ${it.value}" }
            }"
        )
    }
}