package com.tamin.taminhamrah.model.personal.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DependentInfoDTO(
    @SerialName("personal")
    val personal: PersonaDTO?,
    @SerialName("relationWithTamin")
    val tendencyInfo: TendencyInfoDTO?,
)
