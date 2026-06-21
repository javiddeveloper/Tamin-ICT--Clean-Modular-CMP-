package com.tamin.taminhamrah.model.personal.survivorList

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmSurvivorDTO(
    @SerialName("request")val request: RequestModelDTO?
)
