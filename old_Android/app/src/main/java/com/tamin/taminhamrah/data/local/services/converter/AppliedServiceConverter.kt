package com.tamin.taminhamrah.data.local.services.converter

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tamin.taminhamrah.data.entity.ServiceModel


@Keep
class AppliedServiceConverter {
    @TypeConverter
    fun appToString(item: ServiceModel): String =
        Gson().toJson(item)


    @TypeConverter
    fun stringToApp(string: String): ServiceModel =
        Gson().fromJson(string, ServiceModel::class.java)


}