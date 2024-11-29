package com.android.greenmate.domain.model

import java.util.Date

data class Module(
    val myPlantId: Long,
    val lightIntensity: Float,
    val temperature: Float,
    val humidity: Float,
    val soilMoisture: Float,
    val timestamp: Date
)
