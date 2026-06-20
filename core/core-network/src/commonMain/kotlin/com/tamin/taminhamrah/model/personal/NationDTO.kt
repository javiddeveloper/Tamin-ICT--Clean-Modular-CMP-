package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NationDTO(
    @SerialName("statusDate") val statusDate: String?,
    @SerialName("nationDesc") val nationDesc: String?,
    @SerialName("nationCode") val nationCode: String?,
    @SerialName("status") val status: String?,
)
