package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.QueryObject
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference

class ClearUserDataTask {
    fun clearUserData() {
        Task.callInBackground {
            App.instance.getRunningSharedPreference().edit().clear().apply()
            QueryObject.deleteDataFromPointTable()
            QueryObject.deleteDataFromTrackTable()
            QueryObject.deleteDataFromAlarmsTable()
        }
    }
}