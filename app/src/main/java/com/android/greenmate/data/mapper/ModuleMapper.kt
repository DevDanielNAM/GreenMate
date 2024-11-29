package com.android.greenmate.data.mapper

import com.android.greenmate.data.datasource.local.entity.ModuleEntity
import com.android.greenmate.domain.model.Module

fun Module.toEntity(): ModuleEntity {
    return ModuleEntity(
        myPlantId = this.myPlantId,
        lightIntensity = this.lightIntensity,
        temperature = this.temperature,
        humidity = this.humidity,
        soilMoisture = this.soilMoisture,
        timestamp = this.timestamp
    )
}

fun ModuleEntity.toDomainModel(): Module {
    return Module(
        myPlantId = this.myPlantId,
        lightIntensity = this.lightIntensity,
        temperature = this.temperature,
        humidity = this.humidity,
        soilMoisture = this.soilMoisture,
        timestamp = this.timestamp
    )
}