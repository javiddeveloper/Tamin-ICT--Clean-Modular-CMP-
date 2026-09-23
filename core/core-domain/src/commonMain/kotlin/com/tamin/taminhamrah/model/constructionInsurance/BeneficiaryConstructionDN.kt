package com.tamin.taminhamrah.model.constructionInsurance

/** ذینفعان کارگاه — one owner/applicant row for a building-insurance workshop. */
data class BeneficiaryConstructionDN(
    val nationalCode: String?,
    val ownerType: String?,
    val requestNumber: Long?,
    val fileNumber: Long?,
    val requestDate: String?,
    val name: String?,
    val lastName: String?,
    val mobile: String?,
)
