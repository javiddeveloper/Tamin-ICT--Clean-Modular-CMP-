package com.tamin.taminhamrah.model.pregnancyPay

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PregnancyOptionListDTO(
    @SerialName("list") val list: List<PregnancyOptionDTO>? = null,
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier

@Serializable
data class PregnancyOptionDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null,
)
