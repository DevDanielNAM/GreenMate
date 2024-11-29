package com.android.greenmate.domain.model

import java.util.Date

data class Records(
    val myPlantId: Long,
    val title: String,
    val content: String,
    val image: String,
    val date: Date,
)
