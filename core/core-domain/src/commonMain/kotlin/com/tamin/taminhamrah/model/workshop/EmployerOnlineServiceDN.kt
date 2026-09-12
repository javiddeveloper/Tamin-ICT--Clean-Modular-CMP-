package com.tamin.taminhamrah.model.workshop

/**
 * Domain models for خدمات غیرحضوری کارفرما (Employer → Online Services), the legacy
 * `employerEservicesAgreement` flow.
 *
 * Three stepper steps — request OTP ticket → verify code + confirm identity + pick workshop →
 * accept rules + submit — plus two management-side drill-downs (contract rows of a workshop, and
 * the agreements already registered against a workshop).
 *
 * As with [EmployerAgreementDN], strings are non-null and blank when the service omitted them; the
 * display fallback (`-`) is applied once at the presentation edge, not here.
 */

/**
 * The کارفرما identity block returned by `workshop-services/employer-info/{verificationCode}`
 * (stepper step 2).
 *
 * Shown next to the newly requested contact details so the employer can confirm who the agreement
 * is being registered for before accepting the rules in step 3.
 */
data class EmployerContactInfoDN(
    val firstName: String = "",
    val lastName: String = "",
    val nationalCode: String = "",
    /** The employer's *currently registered* mobile, to contrast with the one being requested. */
    val currentMobile: String = "",
    /** The employer's *currently registered* email, to contrast with the one being requested. */
    val currentEmail: String = "",
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

/**
 * One row of `workshop-services/employer-workshops-info-with-out-contract` (stepper step 2): a
 * workshop the employer holds that has no registered contract yet. Selecting one opens its
 * contract rows ([WorkshopContractRowDN]).
 */
data class WorkshopWithoutContractDN(
    val workshopId: String = "",
    val branchCode: String = "",
    val name: String = "",
    val nationalId: String = "",
    val postalCode: String = "",
    val tel: String = "",
    val address: String = "",
    /** From the nested organization block — the branch office, not [branchCode]. */
    val branchOfficeName: String = "",
    val branchOfficeCode: String = "",
) {
    val hasIdentity: Boolean get() = workshopId.isNotBlank() && branchCode.isNotBlank()
}

/**
 * One پیمانکار / contract row of a workshop, from
 * `workshop-services/contract-employer-workshop-info-with-workshop-and-branch-code/...`.
 */
data class WorkshopContractRowDN(
    val contractRow: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val mobile: String = "",
    val email: String = "",
    val nationalCode: String = "",
    val tel: String = "",
    val postalCode: String = "",
    val workshop: WorkshopSummaryDN = WorkshopSummaryDN(),
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

/**
 * One employer-agreement already registered against a single workshop, from
 * `workshop-services/get-employer-agreement-by-workshop-id-and-branch-code/...` (management side).
 *
 * Same shape as [EmployerAgreementDN] but carries [paymentSequence] (`pymseq`), which that list
 * does not.
 */
data class EmployerAgreementByWorkshopDN(
    val paymentSequence: String = "",
    val startDate: String = "",
    val commitmentDate: String = "",
    val email: String = "",
    val mobile: String = "",
    val workshop: WorkshopSummaryDN = WorkshopSummaryDN(),
)

/**
 * The payload of `POST workshop-services/employer-agreement` (stepper step 3).
 *
 * [ticketCode] is the verification code entered in step 2; [mobile]/[email] are the newly
 * requested contact details the agreement is being registered with.
 */
data class EmployerAgreementSubmissionDN(
    val mobile: String,
    val email: String,
    val ticketCode: String,
)
