package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NationDTO(
    @SerialName("statusDate") val statusDate: String? = null,
    @SerialName("nationDesc") val nationDesc: String? = null,
    @SerialName("nationCode") val nationCode: String? = null,
    @SerialName("status") val status: String? = null,
)
