package com.tamin.core.network.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProvinceNameDto(
    @SerialName("list") val list: List<ProvinceDto>,
    @SerialName("total") val total: Int
)
@Serializable
data class ProvinceDto(
    @SerialName("provinceCode") val provinceCode: String,
    @SerialName("provinceName") val provinceName: String?,
    @SerialName("status") val status: String?,
    @SerialName("statusStartDate") val statusStartDate: String?,
)
