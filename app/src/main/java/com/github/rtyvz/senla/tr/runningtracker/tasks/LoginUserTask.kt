package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserLoginResponse

class LoginUserTask {
    fun loginUser(
        userDataRequest: UserDataRequest,
        cancellationToken: CancellationToken
    ): Task<UserLoginResponse> {
        return Task.callInBackground({
            App.api.loginUser(userDataRequest).execute().body()
        }, cancellationToken)
    }
}