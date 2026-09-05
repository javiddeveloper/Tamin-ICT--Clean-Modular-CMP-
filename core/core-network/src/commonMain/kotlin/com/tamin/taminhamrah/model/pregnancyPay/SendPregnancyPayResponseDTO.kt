package com.tamin.taminhamrah.model.pregnancyPay

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendPregnancyPayResponseDTO(
    @SerialName("shorttermRequest") val shorttermRequest: PregnancyResultDTO? = null,
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier

@Serializable
data class PregnancyResultDTO(
    @SerialName("resultMessage") val resultMessage: String? = null,
)
