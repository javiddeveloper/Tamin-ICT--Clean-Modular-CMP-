package com.tamin.taminhamrah.data.local.services.converter

import androidx.annotation.Keep
import androidx.room.TypeConverter
import java.util.Date


@Keep
class TimeStampTypeConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }


}
