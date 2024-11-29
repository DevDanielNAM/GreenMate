package com.android.greenmate.data.datasource.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "plant")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val plantId: Long = 0,
    val engName: String,
    val korName: String,
    val description: String,
    val category: String,
    val image: String,
    val water: String,
    val waterSummary: String,
    val waterDescription: String,
    val waterSummer: String,
    val waterWinter: String,
    val light: String,
    val lightSummary: String,
    val lightDescription: String,
    val lightStrong: String,
    val lightWeak: String,
    val humidity: String,
    val humiditySummary: String,
    val humidityDescription: String,
    val humidityLow: String,
    val humidityHigh: String,
    val temperature: String,
    val temperatureSummary: String,
    val temperatureDescription: String,
    val temperatureWinter: String,
    val temperatureLow: String,
    val temperatureHigh: String
)

