package com.tamin.taminhamrah.model.objectionInsurance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmConflictItemDTO(
    @SerialName("userDesc") val userDesc: String? = null,
)
