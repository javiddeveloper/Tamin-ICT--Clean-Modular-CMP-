package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * What نام‌نویسی غیرحضوری posts to create a registration.
 *
 * The shape is the old app's `NewInsuredUserInfoReq`, which is what `POST employers` accepts.
 * [PersonalRegistrationDTO.nation] and [PersonalRegistrationDTO.countryId] are sent as the
 * constants that app sends: the service requires them and the form never asks for them.
 */
@Serializable
data class NewMemberRegistrationDTO(
    @SerialName("personal") val personal: PersonalRegistrationDTO,
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTaminDTO,
)

@Serializable
data class PersonalRegistrationDTO(
    /** Iranian, and Iran — the only values this flow registers. */
    @SerialName("nation") val nation: String = IRANIAN_NATION_CODE,
    @SerialName("countryId") val countryId: String = IRAN_COUNTRY_ID,
    @SerialName("cityOfBirthId") val cityOfBirthId: String? = null,
    @SerialName("cityOfIssueId") val cityOfIssueId: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("id") val id: Long? = null,
)

@Serializable
data class RelationWithTaminDTO(
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("dateOfStart") val dateOfStart: String? = null,
    @SerialName("job") val job: String? = null,
)

/** What the service answers a create with. */
@Serializable
data class NewMemberRegistrationResultDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("personal") val personal: PersonalRegistrationDTO? = null,
)

/** Whether a national id is someone the organisation has never seen. */
@Serializable
data class NewMemberIsNewDTO(
    @SerialName("isNew") val isNew: Boolean? = null,
    /** Present when the person already exists — the registration then edits rather than creates. */
    @SerialName("personalId") val personalId: Long? = null,
)

private const val IRANIAN_NATION_CODE = "01"
private const val IRAN_COUNTRY_ID = "0001"
