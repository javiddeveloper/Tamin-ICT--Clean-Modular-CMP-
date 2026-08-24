package com.tamin.taminhamrah.feature.taminServices.inspection.ui.model

/** Local, client-side filter over the already-loaded inspection list — never sent to the API. */
data class InspectionSearchCriteria(
    val workshopNo: String = "",
    val inspectionNo: String = "",
) {
    val isEmpty: Boolean get() = workshopNo.isBlank() && inspectionNo.isBlank()

    fun matches(item: InspectionPerformedPR): Boolean {
        val workshopMatches = workshopNo.isBlank() ||
            item.workshopNo.contains(workshopNo.trim(), ignoreCase = true)
        val inspectionMatches = inspectionNo.isBlank() ||
            item.inspectionNo.contains(inspectionNo.trim(), ignoreCase = true)
        return workshopMatches && inspectionMatches
    }
}
