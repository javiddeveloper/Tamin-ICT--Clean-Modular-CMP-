package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegalRepresentativeWorkshopDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("special") val special: Boolean? = null,
    @SerialName("representativeCount") val representativeCount: Int? = null,
)

@Serializable
data class LegalRepresentativeDTO(
    @SerialName("stakeId") val stakeId: Long? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("accessCode") val accessCode: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    // Not present on the legacy Android app's response model — the current design shows a
    // representative's full name, so this is a best-guess field name pending a real API sample.
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("startDate") val startDate: Long? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("special") val special: Boolean? = null,
)

@Serializable
data class LegalRepresentativeRequestDTO(
    @SerialName("accessCode") val accessCode: String,
    @SerialName("branchCode") val branchCode: String?,
    @SerialName("nationalCode") val nationalCode: String?,
    @SerialName("workshopId") val workshopId: String?,
    @SerialName("special") val special: Boolean?,
    // The legacy Android client also duplicated the ticket here even though it's already the
    // {ticket} path segment — kept for fidelity with the confirmed-unchanged backend contract.
    @SerialName("ticket") val ticket: String?,
)
