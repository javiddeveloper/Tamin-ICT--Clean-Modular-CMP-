package com.tamin.taminhamrah.model.requestPaymentForIllDays

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveShortTermIllnessResponseDTO(
    @SerialName("shorttermRequest") val shorttermRequest: IllDaysShortTermResultDTO? = null,
)

@Serializable
data class IllDaysShortTermResultDTO(
    @SerialName("resultMessage") val resultMessage: String? = null,
)
