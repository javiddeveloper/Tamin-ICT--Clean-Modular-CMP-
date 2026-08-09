package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityDTO (
    @SerialName("isDefault") val isDefault: Boolean? = null,
    @SerialName("code") val code: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("type") val type: TypeDTO? = null,
)
