package com.github.rtyvz.senla.tr.runningtracker.db.helpers

import android.database.sqlite.SQLiteDatabase

class CreateTableHelper(private val tableName: String) {

    companion object {
        private const val CREATE_KEY_WORD = "CREATE TABLE IF NOT EXISTS"
        private const val OPEN_BRACKET = "("
        private const val CLOSE_BRACKET = ")"
    }

    private val fields = mutableMapOf<String, String>()
    private val uniqueFields = mutableListOf<String>()

    fun setTableField(fieldName: String, fieldType: String): CreateTableHelper {
        fields[fieldName] = fieldType
        return this
    }

    fun setUniqueFields(listUniqueField: List<String>): CreateTableHelper {
        uniqueFields.addAll(listUniqueField)
        return this
    }

    fun build(db: SQLiteDatabase?) {

        db?.execSQL(
            "$CREATE_KEY_WORD $tableName $OPEN_BRACKET ${
                fields.entries.joinToString
                { "${it.key} ${it.value}" }
            } ${
                when {
                    uniqueFields.isEmpty() -> ""
                    else -> {
                        ",UNIQUE" +
                                uniqueFields.joinToString(
                                    prefix = OPEN_BRACKET,
                                    postfix = CLOSE_BRACKET,
                                    separator = ","
                                ) {
                                    it
                                }
                    }
                }
            }$CLOSE_BRACKET"
        )
    }
}