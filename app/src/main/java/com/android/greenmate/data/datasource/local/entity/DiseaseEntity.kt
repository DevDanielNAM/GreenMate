package com.android.greenmate.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disease")
data class DiseaseEntity(
    @PrimaryKey(autoGenerate = true) val diseaseId: Long = 0,
    val plantId: Long,
    val title: String,
    val descriptions: List<String>
)