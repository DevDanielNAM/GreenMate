package com.android.greenmate.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.greenmate.data.datasource.local.entity.RecordEntity
import java.util.Date

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)

    @Query("Delete FROM record WHERE myPlantId = :myPlantId AND title = :title AND date = :date")
    suspend fun deleteRecord(myPlantId: Long, title: String, date: Date)

    @Query("SELECT * FROM record WHERE recordId = :recordId")
    suspend fun getRecordById(recordId: Long): RecordEntity?

    @Query("SELECT * FROM record WHERE myPlantId = :myPlantId")
    suspend fun getRecordsByMyPlantId(myPlantId: Long): List<RecordEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM record WHERE date BETWEEN :startOfDay AND :endOfDay)")
    suspend fun doesRecordExistForDate(startOfDay: Long, endOfDay: Long): Boolean

    @Query("SELECT * FROM record")
    suspend fun getAllRecords(): List<RecordEntity>
}