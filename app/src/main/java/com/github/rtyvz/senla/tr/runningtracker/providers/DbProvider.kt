package com.github.rtyvz.senla.tr.runningtracker.providers

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb

object DbProvider {
    fun provideDb() = AppDb(App.instance).writableDatabase
}