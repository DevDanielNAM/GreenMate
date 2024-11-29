package com.android.greenmate.data.mapper

import com.android.greenmate.data.datasource.local.entity.RecordEntity
import com.android.greenmate.domain.model.Records

fun Records.toEntity(): RecordEntity {
    return RecordEntity(
        myPlantId = this.myPlantId,
        title = this.title,
        content = this.content,
        image = this.image,
        date = this.date
    )
}

fun RecordEntity.toDomainModel(): Records {
    return Records(
        myPlantId = this.myPlantId,
        title = this.title,
        content = this.content,
        image = this.image,
        date = this.date
    )
}