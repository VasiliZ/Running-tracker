package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.DBHelper
import com.github.rtyvz.senla.tr.runningtracker.extension.getSharedPreference

class ClearUserDataTask {
    fun clearUserData(cancellationToken: CancellationToken) {
        Task.callInBackground({
            App.instance.getSharedPreference().edit().clear().apply()
            DBHelper.deleteDataFromPointTable()
            DBHelper.deleteDataFromTrackTable()
        }, cancellationToken)
    }
}