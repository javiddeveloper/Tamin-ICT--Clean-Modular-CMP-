package com.tamin.taminhamrah.model.pregnancyPay

data class SendPregnancyPayRequestDN(
    val branchCode: String?,
    val branchName: String?,
    val insuranceFirstName: String?,
    val insuranceLastName: String?,
    val mobileNumber: String?,
    val nationalCode: String?,
    val risuid: String?,
    val serviceDateTimeStamp: Int?,
    val pregnancyStatusCode: String?,
    val pregnancyTypeCode: String?,
    val requestTypeCode: String?,
    val restStartDateTimeStamp: Long?,
    val restEndDateTimeStamp: Long?,
    val restDaysCount: String?,
    val babyBirthDateTimeStamp: Long?,
    val doctorName: String?,
    val doctorCode: String?,
    val childNationalId: String?,
    val childNationalId2: String?,
    val childNationalId3: String?,
    val requestFileList: List<PregnancyRequestFileDN>,
)

data class PregnancyRequestFileDN(
    val documentFile: String,
    val documentType: String,
)
