package com.tamin.taminhamrah.model.pregnancyPay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PregnancyOptionListDTO(
    @SerialName("list") val list: List<PregnancyOptionDTO>? = null,
)

@Serializable
data class PregnancyOptionDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null,
)
