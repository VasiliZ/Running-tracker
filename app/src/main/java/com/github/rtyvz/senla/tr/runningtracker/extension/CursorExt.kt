package com.github.rtyvz.senla.tr.runningtracker.extension

import android.database.Cursor

fun <T> Cursor.map(block: (Cursor) -> T) = mutableListOf<T>().also { list ->
    use {
        if (moveToFirst()) {
            do {
                list.add(block.invoke(this))
            } while (moveToNext())
        }
    }
}
