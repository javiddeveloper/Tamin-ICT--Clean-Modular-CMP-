package com.tamin.taminhamrah.model.orotezProtez

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveShortTermOrthosisResponseDTO(
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermOrthosisResultDTO? = null,
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier

@Serializable
data class ShortTermOrthosisResultDTO(
    @SerialName("resultMessage") val resultMessage: String? = null,
)
