package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityDTO (
    @SerialName("parent") val parent: ParentDTO?,
    @SerialName("code") val code: String?,
    @SerialName("description") val description: String?,
    @SerialName("id") val id: Int?,
    @SerialName("title") val title: String?,
    @SerialName("type") val type: TypeDTO?,
)
