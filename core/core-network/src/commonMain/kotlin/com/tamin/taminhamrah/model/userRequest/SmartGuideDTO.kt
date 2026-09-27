package com.tamin.taminhamrah.model.userRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmartGuideDTO(
    @SerialName("id") val id: Long?,
    @SerialName("question") val question: String? = null,
    @SerialName("reply") val reply: String? = null,
    @SerialName("requestCode") val requestCode: String? = null,
    @SerialName("requestDesc") val requestDesc: String? = null,
    @SerialName("isPublic") val isPublic: String? = null, // server sends "1"/"0"
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("requestStatus") val requestStatus: SmartGuideStatusDTO? = null,
    @SerialName("requestType") val requestType: SmartGuideTypeDTO? = null,
)

@Serializable
data class SmartGuideStatusDTO(
    @SerialName("requestCode") val requestCode: String? = null,
    @SerialName("requestDesc") val requestDesc: String? = null,
)

@Serializable
data class SmartGuideTypeDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
)
