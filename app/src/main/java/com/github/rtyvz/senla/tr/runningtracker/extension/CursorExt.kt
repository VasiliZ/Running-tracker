package com.github.rtyvz.senla.tr.runningtracker.extension

import android.database.Cursor

fun <T> Cursor.toList(block: (Cursor) -> T): List<T> {
    return mutableListOf<T>().also {
        if (moveToFirst()) {
            do {
                it.add(block.invoke(this))
            } while (moveToNext())
        }
    }
}