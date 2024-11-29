package com.android.greenmate.data.datasource.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PlantWithDiseases(
    @Embedded val plant: PlantEntity,
    @Relation(
        parentColumn = "plantId",
        entityColumn = "plantId"
    )
    val diseases: List<DiseaseEntity>
)