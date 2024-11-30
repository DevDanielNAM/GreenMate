package com.android.greenmate.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "module",
    foreignKeys = [
        ForeignKey(entity = MyPlantEntity::class, parentColumns = ["myPlantId"], childColumns = ["myPlantId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ModuleEntity(
    @PrimaryKey(autoGenerate = true) val moduleId: Long = 0,
    val myPlantId: Long,
    val lightIntensity: Float,
    val temperature: Float,
    val humidity: Float,
    val soilMoisture: Float,
    val timestamp: Date
)
