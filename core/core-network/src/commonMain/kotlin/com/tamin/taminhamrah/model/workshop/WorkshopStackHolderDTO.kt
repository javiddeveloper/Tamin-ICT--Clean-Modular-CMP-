package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One row of `workshop-services/workshop-stackholders/get-all` — a ذینفع entry. */
@Serializable
data class WorkshopStackHolderDTO(
    @SerialName("stackId") val stackId: Int? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("stackType") val stackType: String? = null,
    @SerialName("birthDate") val birthDate: Long? = null,
    @SerialName("startDate") val startDate: Long? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("telephon") val telephone: String? = null,
    @SerialName("email") val email: String? = null,
    /** Where the displayed name comes from — the row's own columns carry no name. */
    @SerialName("personalRegistrationOffice") val person: StackHolderPersonDTO? = null,
)

@Serializable
data class StackHolderPersonDTO(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
)
