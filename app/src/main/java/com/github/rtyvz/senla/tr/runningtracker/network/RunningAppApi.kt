package com.github.rtyvz.senla.tr.runningtracker.network

import com.github.rtyvz.senla.tr.runningtracker.entity.network.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RunningAppApi {

    @POST("lesson-26.php?method=register")
    fun registerUser(@Body data: UserDataRequest): Call<UserRegisterResponse>

    @POST("lesson-26.php?method=login")
    fun loginUser(@Body data: UserDataRequest): Call<UserLoginResponse>

    @POST("lesson-26.php?method=tracks")
    fun getUserTracks(@Body tracksRequest: TracksRequest): Call<TrackResponse>

    @POST("lesson-26.php?method=save")
    fun saveTrack(@Body track: SaveTrackRequest): Call<SaveTrackResponse>
}