package com.android.greenmate.domain.repository

import com.android.greenmate.domain.model.Module
import kotlinx.coroutines.flow.Flow

interface ModuleRepository {
    suspend fun insertModule(module: Module)
//    suspend fun updateModule(module: Module)
//    suspend fun deleteModule(module: Module)
    suspend fun getModulesByMyPlantId(myPlantId: Long): List<Module>
    suspend fun getModuleById(uniqueId: Long): Module?
    suspend fun getAllModules(): List<Module>
}