package com.github.rtyvz.senla.tr.runningtracker.tasks

import bolts.CancellationToken
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserRegisterResponse

class RegisterUserTask {
    fun registerUser(
        userDataRequest: UserDataRequest,
        cancellationToken: CancellationToken
    ): Task<UserRegisterResponse> {
        return Task.callInBackground({
            App.api.registerUser(userDataRequest).execute()
                .body()
        }, cancellationToken)
    }
}