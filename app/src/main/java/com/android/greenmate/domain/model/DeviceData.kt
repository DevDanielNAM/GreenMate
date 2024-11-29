package com.android.greenmate.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceData (
    val name: String,
    val uuid: String,
    val address: String
): Parcelable
