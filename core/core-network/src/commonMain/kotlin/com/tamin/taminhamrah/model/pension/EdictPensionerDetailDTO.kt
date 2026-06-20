package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EdictPensionerDetailDTO(
    @SerialName("fieldDesc") val fieldDesc: String? = null,
    @SerialName("fieldValue") val fieldValue: String? = "0",
    @SerialName("index") val index: String? = null,
    @SerialName("packageName") val packageName: String? = null,
)
