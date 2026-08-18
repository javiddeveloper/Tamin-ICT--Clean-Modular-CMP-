package com.tamin.taminhamrah.model.userRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestErrorDTO(
    @SerialName("id") val id: Long?,
    @SerialName("errorMassage") val errorMessage: String?,
    @SerialName("errorType") val errorType: String? = null,
    @SerialName("errorStatus") val errorStatus: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
)
