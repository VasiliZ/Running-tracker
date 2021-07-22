package com.github.rtyvz.senla.tr.runningtracker.network

import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserDataRequest
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserLoginResponse
import com.github.rtyvz.senla.tr.runningtracker.entity.network.UserRegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RunningAppApi {

    @POST("lesson-26.php?method=register")
    fun registerUser(@Body data: UserDataRequest): Call<UserRegisterResponse>

    @POST("lesson-26.php?method=login")
    fun loginUser(@Body data: UserDataRequest): Call<UserLoginResponse>
}