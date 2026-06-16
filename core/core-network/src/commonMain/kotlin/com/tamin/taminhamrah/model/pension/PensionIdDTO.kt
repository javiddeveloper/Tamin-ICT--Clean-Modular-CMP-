package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PensionIdDTO(
    @SerialName("pensionerId") val pensionerId: String? = null
)
