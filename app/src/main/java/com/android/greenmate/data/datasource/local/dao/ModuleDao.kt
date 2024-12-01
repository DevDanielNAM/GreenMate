package com.android.greenmate.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.greenmate.data.datasource.local.entity.ModuleEntity


@Dao
interface ModuleDao {
    @Insert
    suspend fun insertModule(module: ModuleEntity)

    @Query("SELECT * FROM module WHERE moduleId = :moduleId")
    suspend fun getModuleById(moduleId: Long): ModuleEntity?

    @Query("SELECT * FROM module WHERE myPlantId = :myPlantId")
    suspend fun getModulesByMyPlantId(myPlantId: Long): List<ModuleEntity>

    @Query("SELECT * FROM module")
    suspend fun getAllModules(): List<ModuleEntity>
}