package com.android.greenmate.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "myplant",
    foreignKeys = [
        ForeignKey(entity = PlantEntity::class, parentColumns = ["plantId"], childColumns = ["plantId"]),
//        ForeignKey(entity = RoomEntity::class, parentColumns = ["roomId"], childColumns = ["roomId"])
    ]
)
data class MyPlantEntity(
    @PrimaryKey(autoGenerate = true) val myPlantId: Long = 0,
    val plantId: Long,
//    val roomId: Long,
    val category: String,
    val alias: String,
    val image: String,
    val favorite: Boolean,
    val date: Date,
)
