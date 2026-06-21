package com.tamin.taminhamrah.model.personal.disabilityRequest

import com.tamin.taminhamrah.model.subDominant.BaseTendency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TendencyInfoDTO(
    @SerialName("relationWithTamin") val baseTendency: BaseTendency? = null,
)
