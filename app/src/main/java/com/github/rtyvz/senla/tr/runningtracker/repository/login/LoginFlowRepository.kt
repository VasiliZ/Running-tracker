package com.github.rtyvz.senla.tr.runningtracker.repository.login

import bolts.CancellationTokenSource
import bolts.Task
import com.github.rtyvz.senla.tr.runningtracker.entity.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.network.ResponseStatus
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserSuccessfulResponse
import com.github.rtyvz.senla.tr.runningtracker.extension.toUserData
import com.github.rtyvz.senla.tr.runningtracker.providers.TasksProvider
import io.reactivex.Observable
import java.util.*

class LoginFlowRepository {

    fun authUser(
            userDataRequest: UserDataRequest,
            callBack: (Result<UserSuccessfulResponse>) -> (Unit)
    ) {
        val cancellationToken = CancellationTokenSource()
        TasksProvider.getRegisterUserTask(userDataRequest, cancellationToken.token)
                .continueWith({
                    if (it.isFaulted) {
                        callBack(Result.Error(it.error.toString()))
                    } else {
                        when (it.result.status) {
                            ResponseStatus.OK ->
                                callBack(Result.Success(UserSuccessfulResponse(it.result.errorCode)))
                            ResponseStatus.ERROR -> callBack(Result.Error(it.result.errorCode))
                        }
                    }
                    return@continueWith it.result.toUserData(
                            userDataRequest.email,
                            userDataRequest.name.toString(),
                            userDataRequest.lastName.toString()
                    )
                }, Task.UI_THREAD_EXECUTOR)
                .continueWith({
                    if (!it.isFaulted && it.result != null) {
                        TasksProvider.getSaveUserDataTask(it.result, cancellationToken.token)
                    }
                }, Task.BACKGROUND_EXECUTOR)
    }

    fun loginUser(
            userDataRequest: UserDataRequest,
            userEmail: String,
            callBack: (Result<UserSuccessfulResponse>) -> Unit
    ) {
        val cancellationToken = CancellationTokenSource()
        TasksProvider.getLoginUserTask(userDataRequest, cancellationToken.token).continueWith({
            if (it.isFaulted) {
                callBack(Result.Error(it.error.toString()))
            } else {
                when (it.result.status) {
                    ResponseStatus.OK ->
                        callBack(Result.Success(UserSuccessfulResponse(it.result.errorCode)))
                    ResponseStatus.ERROR -> callBack(Result.Error(it.result.errorCode))
                }
            }
            return@continueWith it.result.toUserData(userEmail)
        }, Task.UI_THREAD_EXECUTOR)
                .continueWith({
                    if (!it.isFaulted && it.result != null) {
                        TasksProvider.getSaveUserDataTask(it.result, cancellationToken.token)
                    }
                }, Task.BACKGROUND_EXECUTOR)
    }
}