package com.tamin.taminhamrah.model.workshop

/**
 * One کارگاه the signed-in user is registered against as کارفرما.
 *
 * Strings are non-null and empty when the service omitted them, so neither the mapper below nor
 * any screen has to spell the same `?: ""` again; the display fallback (`-`) is applied once, at
 * the presentation edge.
 */
data class EmployerAgreementDN(
    /**
     * ردیف پیمان of this agreement, from `pymseq`.
     *
     * Not the same field as [WorkshopSummaryDN.contractRow]: this one belongs to the *agreement*,
     * that one to the workshop record nested inside it. The ردیف پیمان list shows this one.
     */
    val contractRow: String = "",
    val startDate: String = "",
    val commitmentDate: String = "",
    val email: String = "",
    val mobile: String = "",
    val workshop: WorkshopSummaryDN = WorkshopSummaryDN(),
)

data class WorkshopSummaryDN(
    /** Identity half one. Blank means the row cannot be acted on. */
    val workshopId: String = "",
    /** Identity half two — the branch path segment every downstream service takes. */
    val branchCode: String = "",
    val name: String = "",
    val employerName: String = "",
    val activityName: String = "",
    val address: String = "",
    val registerDate: String = "",
    val approveDate: String = "",
    val contractRow: String = "",
    /** The branch *office* code, which is what the card labels کد شعبه — not [branchCode]. */
    val branchOfficeCode: String = "",
    val branchTitle: String = "",
    val branchOfficeName: String = "",
    /**
     * `01` حقیقی / `02` حقوقی.
     *
     * Kept alongside [characterDescription] because only a حقوقی workshop may have its identity
     * details completed, and that decision cannot be made on the description: the service spells
     * it with an Arabic ي ("حقيقي"), so matching on the text is a spelling coincidence away from
     * offering the form to the wrong workshops.
     */
    val characterCode: String = "",
    /** حقیقی / حقوقی, as the service words it — for display only. */
    val characterDescription: String = "",
    val workshopTypeDescription: String = "",
    val statusCode: String = "",
    val statusDescription: String = "",
) {
    /** Both halves present is the precondition for every action on this workshop. */
    val hasIdentity: Boolean get() = workshopId.isNotBlank() && branchCode.isNotBlank()
}
