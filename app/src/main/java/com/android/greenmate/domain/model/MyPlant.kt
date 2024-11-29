package com.android.greenmate.domain.model

import java.util.Date

data class MyPlant(
    val plantId: Long,
//    val roomId: Long,
    val category: String,
    val alias: String,
    val image: String,
    val favorite: Boolean,
    val date: Date,
)
