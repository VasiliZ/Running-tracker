package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserLoginResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData

fun UserLoginResponse.toUserData(userEmail: String): UserData {
    return UserData(token, name, lastName, userEmail)
}