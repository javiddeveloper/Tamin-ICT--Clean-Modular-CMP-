package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cities",
    indices = [Index(value = ["provinceCode"])],
)
data class CityEntity(
    @PrimaryKey
    val cityCode: String,
    val provinceCode: String?,
    val cityName: String?,
)
