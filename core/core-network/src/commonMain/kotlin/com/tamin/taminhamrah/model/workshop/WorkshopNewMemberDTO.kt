package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of `employers` — a نام نویسی غیر حضوری بیمه شده entry.
 *
 * The row splits in two: the relation-with-Tamin columns are flat on the row, while the person and
 * their registration request arrive nested under `personal`. `personal.request` being null is the
 * draft state — that is what decides which row actions are offered, so it is modelled as nullable
 * all the way through rather than defaulted away.
 */
@Serializable
data class WorkshopNewMemberDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("dateOfStart") val startDate: Long? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("job") val job: String? = null,
    @SerialName("personal") val personal: NewMemberPersonDTO? = null,
)

@Serializable
data class NewMemberPersonDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
    /**
     * The server misspells this as `refrenceCode`. Leave it — a linter "correcting" it to
     * `referenceCode` silently stops the field deserialising.
     */
    @SerialName("refrenceCode") val refrenceCode: String? = null,
    @SerialName("request") val request: NewMemberRequestDTO? = null,
)

@Serializable
data class NewMemberRequestDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("refCode") val refCode: String? = null,
    @SerialName("status") val status: NewMemberRequestStatusDTO? = null,
)

@Serializable
data class NewMemberRequestStatusDTO(
    @SerialName("requestCode") val requestCode: String? = null,
    @SerialName("requestDesc") val requestDesc: String? = null,
)
