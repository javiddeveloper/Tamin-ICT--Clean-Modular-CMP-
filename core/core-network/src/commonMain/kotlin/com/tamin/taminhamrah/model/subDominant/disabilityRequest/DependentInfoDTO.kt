package com.tamin.taminhamrah.model.subDominant.disabilityRequest

import com.tamin.taminhamrah.model.subDominant.Personal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DependentInfoDTO(
    @SerialName("personal")
    val personal: PersonaDTO?,
    @SerialName("relationWithTamin")
    val tendencyInfo: TendencyInfoDTO?,
)
