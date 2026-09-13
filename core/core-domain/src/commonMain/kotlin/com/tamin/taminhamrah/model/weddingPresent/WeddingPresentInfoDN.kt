package com.tamin.taminhamrah.model.weddingPresent

data class WeddingPresentInfoDN(
    val risuid: String? = null,
    val nationalCode: String? = null,
    val insuranceFirstName: String? = null,
    val insuranceLastName: String? = null,
    val mobileNumber: String? = null,
    val insuranceTypeDesc: String? = null,
    val insuranceStatusDesc: String? = null,
    val bankAccount: String? = null,
    val bankName: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val requestHelpType: String? = null,
    val serviceDateTimeStamp: String? = null,
    val request: WeddingPresentRequestDN? = null,
)

data class WeddingPresentRequestDN(
    val id: String? = null,
    val systemType: String? = null,
    val requestDate: Long? = null,
    val userId: String? = null,
    val status: String? = null,
    val requestType: String? = null,
    val editDate: Long? = null,
    val editUser: String? = null,
    val referenceCode: String? = null,
    val branchCode: String? = null,
    val statusName: String? = null,
    val statusId: String? = null,
)

data class WeddingPresentSubmitRequestDN(
    val partnerNationalId: String,
    val weddingDateTimeStamp: Long,
    val info: WeddingPresentInfoDN,
)
