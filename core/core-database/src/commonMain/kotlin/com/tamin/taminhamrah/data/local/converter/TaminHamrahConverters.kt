package com.tamin.taminhamrah.data.local.converter

import androidx.room.TypeConverter
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN

class TaminHamrahConverters {
    @TypeConverter
    fun fromMenuServiceStatus(status: MenuServiceStatusDN?): String? {
        return status?.name
    }

    @TypeConverter
    fun toMenuServiceStatus(name: String?): MenuServiceStatusDN? {
        return name?.let { MenuServiceStatusDN.valueOf(it) }
    }

    @TypeConverter
    fun fromIntList(list: List<Int?>): String {
        return list.joinToString(",") { it?.toString() ?: "" }
    }

    @TypeConverter
    fun toIntList(data: String?): List<Int?> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(",").map { it.toIntOrNull() }
    }
}
