package com.android.greenmate.domain.repository

import com.android.greenmate.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insertAlarm(alarm: Alarm)
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun updateAlarmDone(isDone: Boolean, myPlantId: Long)
    suspend fun deleteAlarm(alarm: Alarm)
    suspend fun getAlarmById(alarmId: Long): Alarm?
    suspend fun getAlarmByTitle(title: String): Alarm?
    suspend fun getAlarmsByMyPlantId(myPlantId: Long): List<Alarm>
    suspend fun getAllAlarms(): List<Alarm>
}