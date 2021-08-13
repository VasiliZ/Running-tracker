package com.github.rtyvz.senla.tr.runningtracker.providers

import com.github.rtyvz.senla.tr.runningtracker.repository.login.LoginFlowRepository
import com.github.rtyvz.senla.tr.runningtracker.repository.main.MainRunningRepository
import com.github.rtyvz.senla.tr.runningtracker.repository.notifications.NotificationRepository

object RepositoryProvider {
    fun provideLoginFlowRepository() = LoginFlowRepository
    fun provideMainRepository() = MainRunningRepository
    fun provideNotificationRepository() = NotificationRepository
}