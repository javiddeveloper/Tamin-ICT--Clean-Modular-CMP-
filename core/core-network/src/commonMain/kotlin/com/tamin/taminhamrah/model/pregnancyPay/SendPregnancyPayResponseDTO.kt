package com.tamin.taminhamrah.model.pregnancyPay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendPregnancyPayResponseDTO(
    @SerialName("shorttermRequest") val shorttermRequest: PregnancyResultDTO? = null,
)

@Serializable
data class PregnancyResultDTO(
    @SerialName("resultMessage") val resultMessage: String? = null,
)
