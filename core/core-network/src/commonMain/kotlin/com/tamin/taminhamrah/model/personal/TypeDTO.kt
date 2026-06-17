package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TypeDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
)
