package com.github.rtyvz.senla.tr.runningtracker.extension

import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserRegisterResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.UserData

fun UserRegisterResponse.toUserData(email: String, name: String, lastName: String) =
    UserData(token, name, lastName, email)
