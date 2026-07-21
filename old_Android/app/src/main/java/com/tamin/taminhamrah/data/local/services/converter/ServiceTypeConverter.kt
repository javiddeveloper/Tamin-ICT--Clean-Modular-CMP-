package com.tamin.taminhamrah.data.local.services.converter

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tamin.taminhamrah.data.local.services.entity.ServiceEntity
import java.lang.reflect.Type

@Keep
class ServiceTypeConverter {

  /*  @TypeConverter
    fun fromString(value: String) = Json.decodeFromString<ServiceEntity?>(value)


    @TypeConverter
    fun fromList(item: ServiceEntity?) =
        Json.encodeToString(item)

*/



    @TypeConverter
    fun fromTransactionList(item: List<ServiceEntity?>?): String? {
        if (item == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<ServiceEntity?>?>() {}.type
        return gson.toJson(item, type)
    }

    @TypeConverter
    fun toTransactionList(itemstring: String?): List<ServiceEntity>? {
        if (itemstring == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<List<ServiceEntity?>?>() {}.type
        return gson.fromJson<List<ServiceEntity>>(itemstring, type)
    }


}
