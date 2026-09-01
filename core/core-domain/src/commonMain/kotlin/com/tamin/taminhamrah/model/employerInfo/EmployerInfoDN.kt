package com.tamin.taminhamrah.model.employerInfo

/** The legal person behind a workshop, looked up from its 11-digit شناسهٔ ملی. */
data class LegalWorkshopDN(
    val name: String?,
    val nationalCode: String?,
)

/** The manager or board member, looked up from their national code and date of birth. */
data class LegalWorkshopCeoDN(
    val firstName: String?,
    val lastName: String?,
) {
    val fullName: String get() = listOfNotNull(firstName, lastName).joinToString(" ").trim()
}

/**
 * What `save-stack-holders` needs.
 *
 * [ceoBirthDateMillis] stays an epoch here on purpose: the service wants it in two different
 * string shapes on two different calls, and choosing between them is the data layer's job.
 */
data class LegalWorkshopInfoRequestDN(
    val workshopId: String,
    val branchCode: String,
    val workshopNationalCode: String,
    val legalWorkshopTypeCode: String,
    val ceoNationalId: String,
    val ceoBirthDateMillis: Long,
    val telephone: String,
    val mobile: String,
    val email: String,
    val ticketCode: String,
)

/** What `save-real-person-info` needs — the branch the request is sent to, and the workshop code. */
data class RealWorkshopInfoRequestDN(
    val branchCode: String,
    val workshopCode: String,
    val ticketCode: String,
)
