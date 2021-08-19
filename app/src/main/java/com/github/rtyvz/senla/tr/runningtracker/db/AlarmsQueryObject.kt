package com.github.rtyvz.senla.tr.runningtracker.db

import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.DeleteDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.InsertDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.SelectDataBuilder
import com.github.rtyvz.senla.tr.runningtracker.db.helpers.UpdateTableBuilder
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity

object AlarmsQueryObject {
    fun deleteDataFromAlarmsTable() {
        DeleteDataBuilder(AppDb.ALARM_TABLE_NAME)
            .build(App.db)
    }

    fun insertNotificationToDb(alarmEntity: AlarmEntity) {
        InsertDataBuilder(AppDb.ALARM_TABLE_NAME)
            .setFieldsWithData(AppDb.ALARM_ID_FIELD_NAME, alarmEntity.alarmId)
            .setFieldsWithData(AppDb.HOUR_FIELD_NAME, alarmEntity.hour)
            .setFieldsWithData(AppDb.MINUTE_FIELD_NAME, alarmEntity.minute)
            .setFieldsWithData(AppDb.TITLE_FIELD_NAME, alarmEntity.title)
            .setFieldsWithData(AppDb.DAY_FIELD_NAME, alarmEntity.day)
            .setFieldsWithData(AppDb.OLD_ID_FIELD_NAME, alarmEntity.oldId)
            .setFieldsWithData(AppDb.IS_ENABLED_NOTIFICATION, alarmEntity.isEnabled)
            .build(App.db)
    }

    fun getNotifications(): List<AlarmEntity> {
        val listNotification = mutableListOf<AlarmEntity>()
        val cursor = SelectDataBuilder(listOf(AppDb.ALARM_TABLE_NAME))
            .fieldFromSelect("${AppDb.ALARM_TABLE_NAME}.*")
            .orderByDesc(AppDb.HOUR_FIELD_NAME)
            .build(App.db)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    listNotification.add(
                        AlarmEntity(
                            alarmId = it.getInt(it.getColumnIndex(AppDb.ALARM_ID_FIELD_NAME)),
                            hour = it.getInt(it.getColumnIndex(AppDb.HOUR_FIELD_NAME)),
                            minute = it.getInt(it.getColumnIndex(AppDb.MINUTE_FIELD_NAME)),
                            title = it.getString(it.getColumnIndex(AppDb.TITLE_FIELD_NAME)),
                            day = it.getLong(it.getColumnIndex(AppDb.DAY_FIELD_NAME)),
                            isEnabled = it.getInt(it.getColumnIndex(AppDb.IS_ENABLED_NOTIFICATION)),
                            oldId = it.getInt(it.getColumnIndex(AppDb.OLD_ID_FIELD_NAME))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return listNotification
    }

    fun updateNotification(alarmEntity: AlarmEntity) {
        UpdateTableBuilder(AppDb.ALARM_TABLE_NAME)
            .setFieldsWithData(AppDb.ALARM_ID_FIELD_NAME, alarmEntity.alarmId)
            .setFieldsWithData(AppDb.HOUR_FIELD_NAME, alarmEntity.hour)
            .setFieldsWithData(AppDb.MINUTE_FIELD_NAME, alarmEntity.minute)
            .setFieldsWithData(AppDb.TITLE_FIELD_NAME, alarmEntity.title)
            .setFieldsWithData(AppDb.DAY_FIELD_NAME, alarmEntity.day)
            .setFieldsWithData(AppDb.IS_ENABLED_NOTIFICATION, alarmEntity.isEnabled)
            .setFieldsWithData(AppDb.OLD_ID_FIELD_NAME, alarmEntity.oldId)
            .whereCondition("${AppDb.ALARM_ID_FIELD_NAME} = ${alarmEntity.oldId}")
            .build(App.db)
    }

    fun deleteNotificationById(alarmId: Int) {
        DeleteDataBuilder(AppDb.ALARM_TABLE_NAME)
            .where("${AppDb.ALARM_ID_FIELD_NAME} = $alarmId")
            .build(App.db)
    }

    fun updateNotificationStateById(alarmId: Int, stateFlag: Int) {
        UpdateTableBuilder(AppDb.ALARM_TABLE_NAME)
            .setFieldsWithData(AppDb.IS_ENABLED_NOTIFICATION, stateFlag)
            .whereCondition("${AppDb.ALARM_ID_FIELD_NAME} = $alarmId")
            .build(App.db)
    }
}