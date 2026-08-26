package com.tamin.taminhamrah.model.requestPaymentForIllDays

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CovidResultListDTO(
    @SerialName("total") val total: Int? = null,
    @SerialName("list") val list: List<String>? = null,
)
