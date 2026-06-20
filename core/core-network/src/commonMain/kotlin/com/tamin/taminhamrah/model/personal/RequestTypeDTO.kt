package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTypeDTO(
    @SerialName("id") val id: Int?,
    @SerialName("title") val title: String?,
    @SerialName("description") val description: String?,
)
