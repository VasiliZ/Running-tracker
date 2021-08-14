package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference

class ClearUserDataTask {
    fun clearUserData() {
        Task.callInBackground {
            App.instance.getRunningSharedPreference().edit().clear().apply()
            DBHelper.deleteDataFromPointTable()
            DBHelper.deleteDataFromTrackTable()
            DBHelper.deleteDataFromAlarmsTable()
        }
    }
}