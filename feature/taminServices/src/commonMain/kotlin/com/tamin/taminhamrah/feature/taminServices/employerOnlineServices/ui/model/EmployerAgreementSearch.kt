package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model

/**
 * Local, client-side filter over the already-loaded تعهدنامه list — never sent to the API, exactly
 * like `inspection`'s `InspectionSearchValidation`. Both fields match against the raw identity values
 * on [EmployerAgreementRowPR], not the pre-formatted labels.
 */
data class EmployerAgreementSearch(
    val branchCode: String = "",
    val workshopCode: String = "",
) {
    val isEmpty: Boolean get() = branchCode.isBlank() && workshopCode.isBlank()

    fun matches(item: EmployerAgreementRowPR): Boolean {
        val branchMatches = branchCode.isBlank() ||
            item.branchCode.contains(branchCode.trim(), ignoreCase = true)
        val workshopMatches = workshopCode.isBlank() ||
            item.workshopId.contains(workshopCode.trim(), ignoreCase = true)
        return branchMatches && workshopMatches
    }
}
