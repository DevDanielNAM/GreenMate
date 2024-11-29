package com.android.greenmate.domain.model

data class Alarm(
    val myPlantId: Long,
    val title: String,
//    val cycle: String,
    val isDone: Boolean
)
