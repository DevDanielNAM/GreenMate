package com.android.greenmate.data.mapper

import com.android.greenmate.data.datasource.local.entity.PlantEntity
import com.android.greenmate.domain.model.Plant

fun Plant.toEntity(): PlantEntity {
    return PlantEntity(
        engName = this.engName,
        korName = this.korName,
        description = this.description,
        category = this.category,
        image = this.image,
        water = this.water,
        waterSummary = this.waterSummary,
        waterDescription = this.waterDescription,
        waterSummer = this.waterSummer,
        waterWinter = this.waterWinter,
        light = this.light,
        lightSummary = this.lightSummary,
        lightDescription = this.lightDescription,
        lightStrong = this.lightStrong,
        lightWeak = this.lightWeak,
        humidity = this.humidity,
        humiditySummary = this.humiditySummary,
        humidityDescription = this.humidityDescription,
        humidityLow = this.humidityLow,
        humidityHigh = this.humidityHigh,
        temperature = this.temperature,
        temperatureSummary = this.temperatureSummary,
        temperatureDescription = this.temperatureDescription,
        temperatureWinter = this.temperatureWinter,
        temperatureLow = this.temperatureLow,
        temperatureHigh = this.temperatureHigh,
    )
}

fun PlantEntity.toDomainModel(): Plant {
    return Plant(
        engName = this.engName,
        korName = this.korName,
        description = this.description,
        category = this.category,
        image = this.image,
        water = this.water,
        waterSummary = this.waterSummary,
        waterDescription = this.waterDescription,
        waterSummer = this.waterSummer,
        waterWinter = this.waterWinter,
        light = this.light,
        lightSummary = this.lightSummary,
        lightDescription = this.lightDescription,
        lightStrong = this.lightStrong,
        lightWeak = this.lightWeak,
        humidity = this.humidity,
        humiditySummary = this.humiditySummary,
        humidityDescription = this.humidityDescription,
        humidityLow = this.humidityLow,
        humidityHigh = this.humidityHigh,
        temperature = this.temperature,
        temperatureSummary = this.temperatureSummary,
        temperatureDescription = this.temperatureDescription,
        temperatureWinter = this.temperatureWinter,
        temperatureLow = this.temperatureLow,
        temperatureHigh = this.temperatureHigh,
    )
}