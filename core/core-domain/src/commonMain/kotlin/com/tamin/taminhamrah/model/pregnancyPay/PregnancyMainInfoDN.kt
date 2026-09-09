package com.tamin.taminhamrah.model.pregnancyPay

data class PregnancyMainInfoDN(
    val risuid: String?,
    val nationalCode: String?,
    val firstName: String?,
    val lastName: String?,
    val mobileNumber: String?,
    val genderCode: String?,
    val serviceDateTimeStamp: Int?,
    val branchWorkshops: List<PregnancyBranchWorkshopDN>,
    val bankAccount: String?,
    val bankName: String?,
    val insuranceTypeDesc: String?,
    val insuranceStatusDesc: String?,
)

data class PregnancyBranchWorkshopDN(
    val branchCode: String?,
    val branchName: String?,
    val workshopName: String?,
)
