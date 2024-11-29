package com.android.greenmate.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "record",
    foreignKeys = [
        ForeignKey(entity = MyPlantEntity::class, parentColumns = ["myPlantId"], childColumns = ["myPlantId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val recordId: Long = 0,
    val myPlantId: Long,
    val title: String,
    val content: String,
    val image: String,
    val date: Date,
)
