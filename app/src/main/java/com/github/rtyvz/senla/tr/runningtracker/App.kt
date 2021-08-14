package com.github.rtyvz.senla.tr.runningtracker

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.github.rtyvz.senla.tr.runningtracker.entity.State
import com.github.rtyvz.senla.tr.runningtracker.network.RunningAppApi
import com.github.rtyvz.senla.tr.runningtracker.providers.DbProvider
import com.github.rtyvz.senla.tr.runningtracker.providers.RepositoryProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    companion object {
        private const val BASE_URL = "https://pub.zame-dev.org/senla-training-addition/"
        lateinit var instance: App
        lateinit var api: RunningAppApi
        lateinit var db: SQLiteDatabase
        lateinit var loggingInterceptor: HttpLoggingInterceptor
        var state: State? = null
        val mainRunningRepository = RepositoryProvider.provideMainRepository()
        val notificationRepository = RepositoryProvider.provideNotificationRepository()
        val loginFlowRepository = RepositoryProvider.provideLoginFlowRepository()
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        db = DbProvider.provideDb()
        loggingInterceptor = HttpLoggingInterceptor()
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
}