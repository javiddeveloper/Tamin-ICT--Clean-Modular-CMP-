package com.tamin.taminhamrah.model.orotezProtez

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveShortTermOrthosisResponseDTO(
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermOrthosisResultDTO? = null,
)

@Serializable
data class ShortTermOrthosisResultDTO(
    @SerialName("resultMessage") val resultMessage: String? = null,
)
