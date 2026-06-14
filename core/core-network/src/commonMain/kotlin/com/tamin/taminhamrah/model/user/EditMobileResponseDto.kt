package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditMobileResponseDto(
    @SerialName("traceId") val traceId: String? = null,
    @SerialName("data") val data: EditMobileDto? = null
)

@Serializable
data class EditMobileDto(
    @SerialName("hash") val hash: String? = null,
    @SerialName("expirationTime") val expirationTime: ExpirationTimeDto? = null
)

@Serializable
data class ExpirationTimeDto(
    @SerialName("year") val year: Int? = null,
    @SerialName("month") val month: String? = null,
    @SerialName("nano") val nano: Long? = null,
    @SerialName("monthValue") val monthValue: Int? = null,
    @SerialName("dayOfMonth") val dayOfMonth: Int? = null,
    @SerialName("hour") val hour: Int? = null,
    @SerialName("minute") val minute: Int? = null,
    @SerialName("second") val second: Int? = null,
    @SerialName("dayOfWeek") val dayOfWeek: String? = null,
    @SerialName("dayOfYear") val dayOfYear: Int? = null,
    @SerialName("chronology") val chronology: ChronologyDto? = null
)

@Serializable
data class ChronologyDto(
    @SerialName("calendarType") val calendarType: String? = null,
    @SerialName("id") val id: String? = null
)
