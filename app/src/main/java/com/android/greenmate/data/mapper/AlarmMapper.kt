package com.android.greenmate.data.mapper

import com.android.greenmate.data.datasource.local.entity.AlarmEntity
import com.android.greenmate.domain.model.Alarm

fun Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        myPlantId = this.myPlantId,
        title = this.title,
//        cycle = this.cycle,
        isDone = this.isDone
    )
}

fun AlarmEntity.toDomainModel(): Alarm {
    return Alarm(
        myPlantId = this.myPlantId,
        title = this.title,
//        cycle = this.cycle,
        isDone = this.isDone
    )
}