package com.tamin.taminhamrah.model.orotezProtez

data class SaveShortTermOrthosisRequestDN(
    val branchCode: String?,
    val branchName: String?,
    val insuranceFirstName: String?,
    val insuranceLastName: String?,
    val mobileNumber: String?,
    val nationalCode: String?,
    val requestFileList: List<ShortTermOrthosisRequestFileDN>,
    val risuid: String?,
    val prescriptionDateTimeStamp: Long?,
    val userNationalCode: String?,
    val userRelation: String?,
    val userRelationship: String?,
    val userFirstName: String?,
    val userInsuredId: String?,
    val userLastName: String?,
)

data class ShortTermOrthosisRequestFileDN(
    val documentFile: String?,
    val documentType: String?,
)
