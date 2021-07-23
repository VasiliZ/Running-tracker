package com.github.rtyvz.senla.tr.runningtracker

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb
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
        lateinit var db: SQLiteDatabase
        lateinit var loginFlowRepository: LoginFlowRepository
        lateinit var mainRunningRepository: MainRunningRepository
        private const val BASE_URL = "https://pub.zame-dev.org/senla-training-addition/"
        private const val GOOGLE_MAPS_API_KEY = "AIzaSyB83mIydx7vFaJw43FW92quNDqHTBJzm0c"
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        api = provideApi()
        db = provideDatabase(this).writableDatabase
        loginFlowRepository = provideLoginFlowRepository()
        mainRunningRepository = provideMainRunningRepository()
    }

    private fun provideApi() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()
        .create(RunningAppApi::class.java)

    //todo move providers to another file
    private fun provideLoginFlowRepository() = LoginFlowRepository()
    private fun provideMainRunningRepository() = MainRunningRepository()
    private fun provideDatabase(context: Context) = AppDb(context)
}