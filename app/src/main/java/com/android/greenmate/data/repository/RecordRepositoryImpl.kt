package com.android.greenmate.data.repository

import com.android.greenmate.data.datasource.local.dao.RecordDao
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.data.mapper.toEntity
import com.android.greenmate.domain.model.Records
import com.android.greenmate.domain.repository.RecordRepository
import java.util.Date

class RecordRepositoryImpl(private val recordDao: RecordDao) : RecordRepository {
    override suspend fun insertRecord(record: Records) {
        recordDao.insertRecord(record.toEntity())
    }

    override suspend fun deleteRecord(myPlantId: Long, title: String, date: Date) {
        recordDao.deleteRecord(myPlantId, title, date)
    }

    override suspend fun getRecordById(recordId: Long): Records? {
        return recordDao.getRecordById(recordId)?.toDomainModel()
    }

    override suspend fun getRecordsByMyPlantId(myPlantId: Long): List<Records> {
        return recordDao.getRecordsByMyPlantId(myPlantId).map { it.toDomainModel() }
    }

    override suspend fun doesRecordExistForDate(startOfDay: Long, endOfDay: Long): Boolean {
        return recordDao.doesRecordExistForDate(startOfDay, endOfDay)
    }

    override suspend fun getAllRecords(): List<Records> {
        return recordDao.getAllRecords().map { it.toDomainModel() }
    }
}