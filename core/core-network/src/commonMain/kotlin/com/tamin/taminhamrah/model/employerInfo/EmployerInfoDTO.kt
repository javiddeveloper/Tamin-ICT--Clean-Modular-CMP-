package com.tamin.taminhamrah.model.employerInfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `workshop-service/legal-inquiry/{id}` — only the name is shown, the rest is ignored. */
@Serializable
data class LegalWorkshopDTO(
    @SerialName("name") val name: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("legalPersonType") val legalPersonType: String? = null,
)

/** `workshop-service/inquiry/{nationalCode}/{birthDate}` — the manager's identity. */
@Serializable
data class LegalWorkshopCeoDTO(
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
)

/**
 * Body of `workshop-service/save-stack-holders`.
 *
 * `telephon` is spelt that way on the wire — the server's field name is the contract. Correcting
 * it to `telephone` makes the landline silently never arrive, and a formatter will happily
 * "fix" it, so the spelling is pinned by [SerialName] rather than left to the property name.
 */
@Serializable
data class LegalWorkshopInfoRequestDTO(
    @SerialName("birthDate") val birthDate: String?,
    @SerialName("branchCode") val branchCode: String?,
    @SerialName("email") val email: String?,
    @SerialName("legalWorkshopTypeCode") val legalWorkshopTypeCode: String?,
    @SerialName("mobile") val mobile: String?,
    @SerialName("nationalId") val nationalId: String?,
    @SerialName("telephon") val telephon: String?,
    @SerialName("ticketCode") val ticketCode: String?,
    @SerialName("workshopId") val workshopId: String?,
    @SerialName("workshopNationalCode") val workshopNationalCode: String?,
)

/**
 * Body of `workshop-service/save-real-person-info`.
 *
 * `brchcode` and `rwshid` are the server's own abbreviations — same rule as [LegalWorkshopInfoRequestDTO].
 */
@Serializable
data class RealWorkshopInfoRequestDTO(
    @SerialName("brchcode") val brchcode: String?,
    @SerialName("rwshid") val rwshid: String?,
    @SerialName("ticketCode") val ticketCode: String?,
)
