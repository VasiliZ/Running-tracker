package com.github.rtyvz.senla.tr.runningtracker.providers

import android.database.sqlite.SQLiteDatabase
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb

object DbProvider {
    fun provideDb(): SQLiteDatabase = AppDb(App.instance).writableDatabase
}