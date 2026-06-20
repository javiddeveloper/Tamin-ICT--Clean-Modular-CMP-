package com.tamin.taminhamrah.model.personal.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DisabilityDependentDTO(
    @SerialName("relationWithTamin")
    val relationWithTamin: DependentInfoDTO?
)
