package com.tamin.taminhamrah.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInsuredInfoDTO(
    @SerialName("total") val total: Int? = null,
    @SerialName("list") val list: List<String>? = null,
    @SerialName("typeUser") val typeUser: String? = null,
)
