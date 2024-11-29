package com.android.greenmate.data.repository

import com.android.greenmate.data.datasource.local.dao.ModuleDao
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.data.mapper.toEntity
import com.android.greenmate.domain.model.Module
import com.android.greenmate.domain.repository.ModuleRepository

class ModuleRepositoryImpl(private val moduleDao: ModuleDao) : ModuleRepository {
    override suspend fun insertModule(module: Module) {
        moduleDao.insertModule(module.toEntity())
    }

    override suspend fun getModuleById(uniqueId: Long): Module? {
        return moduleDao.getModuleById(uniqueId)?.toDomainModel()
    }

    override suspend fun getModulesByMyPlantId(myPlantId: Long): List<Module> {
        return moduleDao.getModulesByMyPlantId(myPlantId).map { it.toDomainModel() }
    }

    override suspend fun getAllModules(): List<Module> {
        return moduleDao.getAllModules().map { it.toDomainModel() }
    }
}