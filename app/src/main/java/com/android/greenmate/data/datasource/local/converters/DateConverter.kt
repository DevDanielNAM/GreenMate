package com.android.greenmate.data.datasource.local.converters

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
