package com.github.rtyvz.senla.tr.runningtracker

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github.rtyvz.senla.tr.runningtracker.db.AppDb
import com.github.rtyvz.senla.tr.runningtracker.entity.State
import com.github.rtyvz.senla.tr.runningtracker.network.RunningAppApi
import com.github.rtyvz.senla.tr.runningtracker.repository.login.LoginFlowRepository
import com.github.rtyvz.senla.tr.runningtracker.repository.main.MainRunningRepository
import com.github.rtyvz.senla.tr.runningtracker.repository.notifications.NotificationRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    companion object {
        lateinit var instance: App
        var state: State? = null
        lateinit var api: RunningAppApi
        lateinit var db: SQLiteDatabase
        lateinit var loginFlowRepository: LoginFlowRepository
        lateinit var mainRunningRepository: MainRunningRepository
        lateinit var notificationRepository: NotificationRepository
        lateinit var loggingInterceptor: HttpLoggingInterceptor
        private const val BASE_URL = "https://pub.zame-dev.org/senla-training-addition/"
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        db = provideDatabase(this).writableDatabase
        loginFlowRepository = provideLoginFlowRepository()
        mainRunningRepository = provideMainRunningRepository()
        loggingInterceptor = HttpLoggingInterceptor()
        notificationRepository = NotificationRepository
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        api = provideApi()
    }

    private fun provideOkHttpClient() =
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    private fun provideApi() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(provideOkHttpClient())
        .build()
        .create(RunningAppApi::class.java)

    //todo move providers to another file
    private fun provideLoginFlowRepository() = LoginFlowRepository()
    private fun provideMainRunningRepository() = MainRunningRepository()
    private fun provideDatabase(context: Context) = AppDb(context)
}