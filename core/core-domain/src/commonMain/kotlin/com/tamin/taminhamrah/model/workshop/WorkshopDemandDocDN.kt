package com.tamin.taminhamrah.model.workshop

/** One سند مطالبه of a debt. */
data class WorkshopDemandDocDN(
    val docNumber: String = "",
    val docDate: String = "",
    val docTypeDescription: String = "",
    val debitStepDescription: String = "",
    val debitStateDescription: String = "",
) {
    /** Only a row with a document number can be opened as a PDF. */
    val isViewable: Boolean get() = docNumber.isNotBlank()
}
