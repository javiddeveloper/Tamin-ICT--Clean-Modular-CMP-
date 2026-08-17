package com.tamin.taminhamrah.model.orotezProtez

data class SaveShortTermOrthosisRequestDN(
    val branchCode: String?,
    val branchName: String?,
    val insuranceFirstName: String?,
    val insuranceLastName: String?,
    val mobileNumber: String?,
    val nationalCode: String?,
    val requestId: String?,
    val requestFileList: List<ShortTermOrthosisRequestFileDN>,
    val requestHelpType: String?,
    val risuid: String?,
    val serviceDateTimeStamp: Long?,
    val userNationalCode: String?,
    val userRelation: String?,
    val userRelationship: String?,
    val userFirstName: String?,
    val userInsuredId: String?,
    val userLastName: String?,
    val userBirthDateTimeStamp: Long?,
)

data class ShortTermOrthosisRequestFileDN(
    val documentFile: String?,
    val documentType: String?,
)
