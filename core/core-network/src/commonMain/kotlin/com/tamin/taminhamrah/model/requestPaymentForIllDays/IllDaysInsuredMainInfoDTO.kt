package com.tamin.taminhamrah.model.requestPaymentForIllDays

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class IllDaysInsuredMainInfoDTO(
    @SerialName("bankAccount") val bankAccount: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("branchWorkshop") val branchWorkshop: List<IllDaysBranchWorkshopDTO>? = null,
    @SerialName("flag") val flag: Boolean? = null,
    @SerialName("genderCode") val genderCode: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("insuranceStatusDesc") val insuranceStatusDesc: String? = null,
    @SerialName("insuranceTypeDesc") val insuranceTypeDesc: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("requestHelpType") val requestHelpType: String? = null,
    @SerialName("requestHelpTypeDesc") val requestHelpTypeDesc: String? = null,
    @SerialName("resultMessage") val resultMessage: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("serviceDateTimeStamp") val serviceDateTimeStamp: Long? = null,
    @SerialName("shorttemRequestId") val shortTermRequestId: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("requestFileList") val requestFileList: List<JsonElement>? = null,
)

@Serializable
data class IllDaysBranchWorkshopDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)
