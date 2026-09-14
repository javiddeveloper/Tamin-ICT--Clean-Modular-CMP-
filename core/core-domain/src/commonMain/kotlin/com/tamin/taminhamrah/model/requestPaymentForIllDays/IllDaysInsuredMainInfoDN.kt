package com.tamin.taminhamrah.model.requestPaymentForIllDays

data class IllDaysInsuredMainInfoDN(
    val risuid: String?,
    val nationalCode: String?,
    val firstName: String?,
    val lastName: String?,
    val mobileNumber: String?,
    val genderCode: String?,
    val branchCode: String?,
    val branchName: String?,
    val bankAccount: String?,
    val bankName: String?,
    val insuranceTypeDesc: String?,
    val insuranceStatusDesc: String?,
    val serviceDateTimeStamp: Long?,
    val branchWorkshops: List<IllDaysBranchWorkshopDN>,
)

data class IllDaysBranchWorkshopDN(
    val branchCode: String?,
    val branchName: String?,
    val workshopCode: String?,
    val workshopName: String?,
)
