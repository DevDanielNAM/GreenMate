package com.android.greenmate.data.repository

import com.android.greenmate.data.datasource.local.dao.AlarmDao
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.data.mapper.toEntity
import com.android.greenmate.domain.model.Alarm
import com.android.greenmate.domain.repository.AlarmRepository

class AlarmRepositoryImpl(private val alarmDao: AlarmDao) : AlarmRepository {
    override suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm.toEntity())
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.toEntity())
    }

    override suspend fun updateAlarmDone(isDone: Boolean, myPlantId: Long) {
        alarmDao.updateAlarmDone(isDone, myPlantId)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm.toEntity())
    }

    override suspend fun getAlarmById(alarmId: Long): Alarm? {
        return alarmDao.getAlarmById(alarmId)?.toDomainModel()
    }

    override suspend fun getAlarmByTitle(title: String): Alarm? {
        return alarmDao.getAlarmByTitle(title)?.toDomainModel()
    }

    override suspend fun getAlarmsByMyPlantId(myPlantId: Long): List<Alarm> {
        return alarmDao.getAlarmsByMyPlantId(myPlantId).map { it.toDomainModel() }
    }

    override suspend fun getAllAlarms(): List<Alarm> {
        return alarmDao.getAllAlarms().map { it.toDomainModel() }
    }
}