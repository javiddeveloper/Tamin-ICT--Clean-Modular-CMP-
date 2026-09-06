package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model

/**
 * One تعهدنامه card in the Employer Online Services agreements list
 * ([com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerAgreementCard]).
 *
 * Every label is pre-formatted at the mapping edge (Persian digits, separated Jalali dates, dashed
 * blanks). [workshopId] / [branchCode] are kept raw because they are identity, not display — they
 * travel to the "ردیف‌های پیمان" drill-down and into its query parameters.
 */
data class EmployerAgreementRowPR(
    val workshopId: String = "",
    val branchCode: String = "",
    /** Both identity halves present — the precondition for the "ردیف‌های پیمان" action. */
    val hasIdentity: Boolean = false,
    val isActive: Boolean = false,
    val statusLabel: String = "",
    val workshopName: String = "",
    /** «شعبهٔ ۲ مشهد · ۱۲۰۲» — branch office name and code, joined for the شعبه slot. */
    val branchLabel: String = "",
    /** [workshopId] as the card prints it, under شماره کارگاه. */
    val workshopCodeLabel: String = "",
    val commitmentDate: String = "",
    // --- only shown once جزئیات is expanded ---
    val startDate: String = "",
    val address: String = "",
    val mobile: String = "",
    val email: String = "",
)
