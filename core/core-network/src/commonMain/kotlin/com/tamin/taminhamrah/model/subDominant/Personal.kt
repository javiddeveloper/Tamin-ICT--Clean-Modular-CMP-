package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Personal(
    // Raw epoch-millis timestamp (e.g. -584681400000), not a preformatted string — the backend
    // sends a JSON number here. Convert with PersianDateFormatter.formatTimestamp() for display.
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("gender") val gender: Gender? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    @SerialName("idCardSerial1") val idCardSerial1: String? = null,
    @SerialName("idCardSerial2") val idCardSerial2: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
)
