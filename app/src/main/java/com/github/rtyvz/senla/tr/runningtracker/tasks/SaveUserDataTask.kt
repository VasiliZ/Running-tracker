package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference

class SaveUserDataTask {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
    }

    fun saveUserData(userData: UserData, cancellationToken: CancellationToken) {
        Task.callInBackground({
            val prefs = App.instance.getRunningSharedPreference()
            prefs.edit().putString(USER_TOKEN, userData.token).apply()
            prefs.edit().putString(USER_NAME, userData.name).apply()
            prefs.edit().putString(USER_LAST_NAME, userData.lastName).apply()
            prefs.edit().putString(USER_EMAIL, userData.email).apply()
        }, cancellationToken)
    }
}