package com.android.greenmate.domain.repository

import com.android.greenmate.data.datasource.local.entity.RecordEntity
import com.android.greenmate.domain.model.Records
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RecordRepository {
    suspend fun insertRecord(record: Records)
//    suspend fun updateRecord(record: Record)
    suspend fun deleteRecord(myPlantId: Long, title: String, date: Date)
    suspend fun getRecordById(recordId: Long): Records?
    suspend fun getRecordsByMyPlantId(myPlantId: Long): List<Records>
    suspend fun doesRecordExistForDate(startOfDay: Long, endOfDay: Long): Boolean
    suspend fun getAllRecords(): List<Records>
}