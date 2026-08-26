package com.tamin.taminhamrah.model.historyObjection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmNotExistItemDTO(
    @SerialName("userDesc") val userDesc: String? = null,
)
