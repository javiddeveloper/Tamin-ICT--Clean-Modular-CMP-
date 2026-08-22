package com.tamin.taminhamrah.model.pension

data class PensionInquiryDN(
    val branchCode: String?,
    val insuranceNumber: String?,
    val pensionerRisUid: String?,
    val pensionerType: String?,
    val paymentDate: String?,
    val pensionerBaseDate: String?,
    val fullName: String?,
    val statusDesc: String?,
    val sexDesc: String?,
    val branchName: String?,
    val pensionEndDate: String?,
    val nationalId: String?,
    val paymentAmount: Int?,
    val pensionerId: String? = null,
    val pensionerTypeDesc: String? = null,
)
