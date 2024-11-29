package com.android.greenmate.data.repository

import com.android.greenmate.data.datasource.local.dao.PlantDao
import com.android.greenmate.data.datasource.local.entity.DiseaseEntity
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.data.mapper.toEntity
import com.android.greenmate.domain.model.Plant
import com.android.greenmate.domain.repository.PlantRepository

class PlantRepositoryImpl(private val plantDao: PlantDao) : PlantRepository {
    override suspend fun insertPlantsAll(plants: List<Plant>) {
        plantDao.insertPlantsAll(plants.map { it.toEntity() })
    }

    override suspend fun getPlantById(plantId: Long): Plant? {
        return plantDao.getPlantById(plantId)?.toDomainModel()
    }

    override suspend fun getPlantByTitle(korName: String): Plant? {
        return plantDao.getPlantByTitle(korName)?.toDomainModel()
    }

    override suspend fun getPlantByPartialTitle(korName: String): List<Plant> {
        return plantDao.getPlantByPartialTitle(korName).map { it.toDomainModel() }
    }

    override suspend fun getAllPlants(): List<Plant> {
        return plantDao.getAllPlants().map { it.toDomainModel() }
    }

    override suspend fun getPlantIdByTitle(korName: String): Plant? {
        return plantDao.getPlantIdByTitle(korName)?.toDomainModel()
    }

    override suspend fun getDiseasesByPlantId(plantId: Long): List<DiseaseEntity> {
        return plantDao.getDiseasesByPlantId(plantId)
    }
}