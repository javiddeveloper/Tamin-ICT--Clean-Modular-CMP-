package com.tamin.taminhamrah.data.local.services.converter

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tamin.taminhamrah.data.local.services.entity.ServiceEntity


@Keep
class LastSeenServiceTypeConverter {

    @TypeConverter
    fun appToString(item: ServiceEntity?): String? {
        return if (item != null)
            Gson().toJson(item)
        else null
    }

    @TypeConverter
    fun stringToApp(string: String?): ServiceEntity? {
        return if (string != null)
            Gson().fromJson(string, ServiceEntity::class.java)
        else null
    }


}
