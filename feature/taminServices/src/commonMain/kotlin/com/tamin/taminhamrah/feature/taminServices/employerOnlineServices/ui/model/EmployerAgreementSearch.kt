package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model

/**
 * کد شعبه / کد کارگاه search criteria for the تعهدنامه list — sent to the server as the
 * `workshop.branchCode` / `workshop.workshopId` filter (see `WorkShopsRepositoryImpl.getEmployerAgreements`),
 * exactly like the old app's search dialog. The list only ever holds one loaded window of results, so
 * a search re-queries the server rather than filtering what happens to already be on screen.
 */
data class EmployerAgreementSearch(
    val branchCode: String = "",
    val workshopCode: String = "",
) {
    val isEmpty: Boolean get() = branchCode.isBlank() && workshopCode.isBlank()
}
