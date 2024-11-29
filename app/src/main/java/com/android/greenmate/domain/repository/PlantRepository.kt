package com.android.greenmate.domain.repository

import com.android.greenmate.data.datasource.local.entity.DiseaseEntity
import com.android.greenmate.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun insertPlantsAll(plants: List<Plant>)
    suspend fun getPlantById(plantId: Long): Plant?
    suspend fun getPlantByTitle(korName: String): Plant?
    suspend fun getPlantByPartialTitle(korName: String): List<Plant>
    suspend fun getAllPlants(): List<Plant>
    suspend fun getPlantIdByTitle(korName: String): Plant?
    suspend fun getDiseasesByPlantId(plantId: Long): List<DiseaseEntity>
}