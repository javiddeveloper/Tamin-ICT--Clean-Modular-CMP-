package com.tamin.taminhamrah.model.constructionInsurance

/** ذینفعان کارگاه — presentation model; display formatting (labels/rows) belongs to the UI layer. */
data class BeneficiaryConstructionPR(
    val nationalCode: String? = null,
    val ownerType: String? = null,
    val requestNumber: Long? = null,
    val fileNumber: Long? = null,
    val requestDate: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val mobile: String? = null,
) {
    /** ownerType "01" = مالک, "02" = متقاضی — see the old app's `BeneficiariesConstructionModel`. */
    val isOwner: Boolean get() = ownerType == "01"
    val isApplicant: Boolean get() = ownerType == "02"
}
