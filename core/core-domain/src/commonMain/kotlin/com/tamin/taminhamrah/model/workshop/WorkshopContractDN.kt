package com.tamin.taminhamrah.model.workshop

/**
 * One ردیف پیمان of a workshop that has no تعهدنامه on file.
 *
 * The lean half of the pair: the service sends no contact details for these rows, so the card
 * shows four fields where an [EmployerAgreementDN] row shows seven. That difference is in the data,
 * not in a display flag — there is nothing here to omit.
 *
 * Strings are non-null and empty when the service omitted them; the display fallback is applied
 * once, at the presentation edge.
 */
data class WorkshopContractDN(
    /** ردیف پیمان. Arrives as `contractRow`, against `pymseq` on the sibling endpoint. */
    val contractRow: String = "",
    /** تاریخ تعهد, compact Jalali. */
    val startDate: String = "",
    val workshopId: String = "",
    val branchCode: String = "",
    val workshopName: String = "",
)
