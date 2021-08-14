package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.extension.putString

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
            prefs.putString(USER_TOKEN, userData.token)
            prefs.putString(USER_NAME, userData.name)
            prefs.putString(USER_LAST_NAME, userData.lastName)
            prefs.putString(USER_EMAIL, userData.email)
        }, cancellationToken)
    }
}