package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Code/description pairs that the workshop services hang off every workshop object.
 * They are shared by the employer-agreement list, the member list and the stakeholder list,
 * which is why they live here instead of being re-declared per response.
 */

@Serializable
data class WorkshopBranchDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("organizationName") val organizationName: String? = null,
)

/** `characterCode` is `01` حقیقی / `02` حقوقی. */
@Serializable
data class WorkshopCharacterDTO(
    @SerialName("characterCode") val characterCode: String? = null,
    @SerialName("characterDesc") val characterDesc: String? = null,
)

/**
 * The server spells these two keys `workshoptype…` — lower-case `t`, unlike every neighbouring
 * field. That is the contract; "fixing" the spelling makes the field stop deserialising.
 */
@Serializable
data class WorkshopTypeDTO(
    @SerialName("workshoptypeCode") val workshopTypeCode: String? = null,
    @SerialName("workshoptypeDesc") val workshopTypeDesc: String? = null,
)

/** `workshopStatusCode` is `01` فعال / `02` نیمه فعال / `03` غیر فعال. */
@Serializable
data class WorkshopStatusDTO(
    @SerialName("workshopStatusCode") val workshopStatusCode: String? = null,
    @SerialName("workshopStatusDesc") val workshopStatusDesc: String? = null,
)

@Serializable
data class WorkshopNationDTO(
    @SerialName("nationCode") val nationCode: String? = null,
    @SerialName("nationDesc") val nationDesc: String? = null,
)
