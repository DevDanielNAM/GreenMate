package com.android.greenmate.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.android.greenmate.data.datasource.local.entity.DiseaseEntity
import com.android.greenmate.data.datasource.local.entity.PlantEntity
import com.android.greenmate.data.datasource.local.entity.PlantWithDiseases

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantsAll(plants: List<PlantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiseases(diseases: List<DiseaseEntity>)

    @Transaction
    @Query("SELECT * FROM plant WHERE plantId = :plantId")
    suspend fun getPlantWithDiseases(plantId: Long): PlantWithDiseases

    @Query("SELECT * FROM plant WHERE plantId = :plantId")
    suspend fun getPlantById(plantId: Long): PlantEntity?

    @Query("SELECT * FROM plant WHERE korName = :korName")
    suspend fun getPlantByTitle(korName: String): PlantEntity?

    @Query("SELECT * FROM plant")
    suspend fun getAllPlants(): List<PlantEntity>

    @Query("SELECT * FROM plant WHERE korName LIKE '%' || :korName || '%' COLLATE NOCASE")
    suspend fun getPlantByPartialTitle(korName: String): List<PlantEntity>

    @Query("SELECT * FROM plant WHERE korName = :korName")
    suspend fun getPlantIdByTitle(korName: String?): PlantEntity?

    @Query("SELECT * FROM disease WHERE plantId = :plantId")
    suspend fun getDiseasesByPlantId(plantId: Long): List<DiseaseEntity>
}