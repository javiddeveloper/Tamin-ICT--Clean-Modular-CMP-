package com.tamin.taminhamrah.model.requestPaymentForIllDays

data class SaveShortTermIllnessRequestDN(
    val doctorId: String?,
    val doctorName: String?,
    val startDateTimeStamp: Long?,
    val endDateTimeStamp: Long?,
    val illnessKind: String?,
    val workStatus: String?,
    val provinceCode: String?,
    val cityCode: String?,
    val branchCode: String?,
    val branchName: String?,
    val insuranceFirstName: String?,
    val insuranceLastName: String?,
    val mobileNumber: String?,
    val nationalCode: String?,
    val risuid: String?,
    val serviceDateTimeStamp: Long?,
    val requestFileList: List<IllDaysRequestFileDN>,
)

data class IllDaysRequestFileDN(
    val documentFile: String?,
    val documentType: String?,
)
