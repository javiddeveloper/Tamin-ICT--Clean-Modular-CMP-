package com.tamin.taminhamrah.model.pregnancyPay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendPregnancyPayRequestDTO(
    @SerialName("barChild") val barChild: String? = null,
    @SerialName("barDRid") val doctorCode: String? = null,
    @SerialName("barDd") val restDaysCount: String? = null,
    @SerialName("barDemDatTimeStamp") val babyBirthDateTimeStamp: Long? = null,
    @SerialName("barDrname") val doctorName: String? = null,
    @SerialName("barEDateTimeStamp") val restEndDateTimeStamp: Long? = null,
    @SerialName("barSDateTimeStamp") val restStartDateTimeStamp: Long? = null,
    @SerialName("barType") val barType: String? = null,
    @SerialName("childNationalId") val childNationalId: String? = null,
    @SerialName("childNationalId2") val childNationalId2: String? = null,
    @SerialName("childNationalId3") val childNationalId3: String? = null,
    @SerialName("wrkPart") val requestTypeCode: String? = null,
    @SerialName("shorttermRequest") val shorttermRequest: PregnancyShorttermRequestDTO? = null,
)

@Serializable
data class PregnancyShorttermRequestDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("requestFileList") val requestFileList: List<PregnancyRequestFileDTO>? = null,
    @SerialName("requestHelpType") val requestHelpType: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("serviceDateTimeStamp") val serviceDateTimeStamp: Int? = null,
)

@Serializable
data class PregnancyRequestFileDTO(
    @SerialName("documentFile") val documentFile: String? = null,
    @SerialName("documentType") val documentType: String? = null,
)
