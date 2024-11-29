package com.android.greenmate.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarm",
    foreignKeys = [
        ForeignKey(entity = MyPlantEntity::class, parentColumns = ["myPlantId"], childColumns = ["myPlantId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val alarmId: Long = 0,
    val myPlantId: Long,
    val title: String,
//    val cycle: String,
    val isDone: Boolean
)
