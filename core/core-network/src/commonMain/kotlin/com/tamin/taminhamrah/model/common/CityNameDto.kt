package com.tamin.core.network.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityNameDto(
    @SerialName("list") val list: List<CityDto>,
    @SerialName("total") val total: Int
)

@Serializable
data class CityDto(
    @SerialName("cityCode") val cityCode: String,
    @SerialName("provincecode") val provinceCode: String? = null,
    @SerialName("cityName") val cityName: String? = null
)