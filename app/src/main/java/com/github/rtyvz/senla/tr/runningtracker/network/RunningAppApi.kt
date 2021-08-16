package com.github.rtyvz.senla.tr.runningtracker.network

import com.github.rtyvz.senla.tr.runningtracker.entity.network.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RunningAppApi {

    @POST(Endpoint.REGISTER_USER)
    fun registerUser(@Body data: UserDataRequest): Call<UserRegisterResponse>

    @POST(Endpoint.LOGIN_USER)
    fun loginUser(@Body data: UserDataRequest): Call<UserLoginResponse>

    @POST(Endpoint.GET_USER_TRACKS)
    fun getUserTracks(@Body tracksRequest: TracksRequest): Call<TrackResponse>

    @POST(Endpoint.SAVE_USER_TRACK)
    fun saveTrack(@Body track: SaveTrackRequest): Call<SaveTrackResponse>

    @POST(Endpoint.GET_TRACK_POINTS)
    fun getPoints(@Body pointsRequest: PointsRequest): Call<PointResponse>
}