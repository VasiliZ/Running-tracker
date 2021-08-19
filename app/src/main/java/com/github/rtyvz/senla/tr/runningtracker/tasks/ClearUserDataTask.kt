package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.AlarmsQueryObject
import com.github.rtyvz.senla.tr.runningtracker.db.PointsQueryObject
import com.github.rtyvz.senla.tr.runningtracker.db.TrackQueryObject
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference

class ClearUserDataTask {
    fun clearUserData() {
        Task.callInBackground {
            App.instance.getRunningSharedPreference().edit().clear().apply()
            PointsQueryObject.deleteDataFromPointTable()
            TrackQueryObject.deleteDataFromTrackTable()
            AlarmsQueryObject.deleteDataFromAlarmsTable()
        }
    }
}