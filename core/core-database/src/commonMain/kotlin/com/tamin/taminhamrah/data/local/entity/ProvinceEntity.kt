package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provinces")
data class ProvinceEntity(
    @PrimaryKey
    val provinceCode: String,
    val provinceName: String?,
    val status: String?,
    val statusStartDate: String?,
)
