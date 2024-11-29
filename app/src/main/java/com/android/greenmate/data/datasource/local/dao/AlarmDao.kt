package com.android.greenmate.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.greenmate.data.datasource.local.entity.AlarmEntity

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Query("UPDATE alarm SET isDone =:isDone  WHERE myPlantId =:myPlantId")
    suspend fun updateAlarmDone(isDone: Boolean, myPlantId: Long)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("SELECT * FROM alarm WHERE alarmId = :alarmId")
    suspend fun getAlarmById(alarmId: Long): AlarmEntity?

    @Query("SELECT * FROM alarm WHERE title = :title")
    suspend fun getAlarmByTitle(title: String): AlarmEntity?

    @Query("SELECT * FROM alarm WHERE myPlantId = :myPlantId")
    suspend fun getAlarmsByMyPlantId(myPlantId: Long): List<AlarmEntity>

    @Query("SELECT * FROM alarm")
    suspend fun getAllAlarms(): List<AlarmEntity>
}