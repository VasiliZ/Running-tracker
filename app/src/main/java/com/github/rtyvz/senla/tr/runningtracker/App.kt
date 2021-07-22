package com.github.rtyvz.senla.tr.runningtracker

import android.app.Application
import com.github.rtyvz.senla.tr.runningtracker.network.RunningAppApi
import com.github.rtyvz.senla.tr.runningtracker.repository.login.LoginFlowRepository
import com.github.rtyvz.senla.tr.runningtracker.repository.main.MainRunningRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    companion object {
        lateinit var instance: App
        lateinit var api: RunningAppApi
        lateinit var loginFlowRepository: LoginFlowRepository
        lateinit var mainRunningRepository: MainRunningRepository
        private const val BASE_URL = "https://pub.zame-dev.org/senla-training-addition/"
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        api = provideApi()
        loginFlowRepository = provideLoginFlowRepository()
        mainRunningRepository = provideMainRunningRepository()
    }

    private fun provideApi() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()
        .create(RunningAppApi::class.java)

    private fun provideLoginFlowRepository() = LoginFlowRepository()
    private fun provideMainRunningRepository() = MainRunningRepository()
}