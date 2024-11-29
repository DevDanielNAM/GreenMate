package com.android.greenmate.data.mapper

import com.android.greenmate.data.datasource.local.entity.MyPlantEntity
import com.android.greenmate.domain.model.MyPlant

fun MyPlant.toEntity(): MyPlantEntity {
    return MyPlantEntity(
        plantId = this.plantId,
//        roomId = this.roomId,
        category = this.category,
        alias = this.alias,
        image = this.image,
        favorite = this.favorite,
        date =  this.date
    )
}

fun MyPlantEntity.toDomainModel(): MyPlant {
    return MyPlant(
        plantId = this.plantId,
//        roomId = this.roomId,
        category = this.category,
        alias = this.alias,
        image = this.image,
        favorite = this.favorite,
        date =  this.date
    )
}