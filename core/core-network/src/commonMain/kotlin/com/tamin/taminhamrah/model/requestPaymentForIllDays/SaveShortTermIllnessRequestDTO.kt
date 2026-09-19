package com.tamin.taminhamrah.model.requestPaymentForIllDays

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveShortTermIllnessRequestDTO(
    @SerialName("bimDrid") val doctorId: String? = null,
    @SerialName("bimDrname") val doctorName: String? = null,
    @SerialName("bimEdateTimeStamp") val endDateTimeStamp: Long? = null,
    @SerialName("bimKind") val illnessKind: String? = null,
    @SerialName("bimSdateTimeStamp") val startDateTimeStamp: Long? = null,
    @SerialName("bimWkstatus") val workStatus: String? = null,
    @SerialName("provinceCode") val provinceCode: String? = null,
    @SerialName("cityCode") val cityCode: String? = null,
    @SerialName("shorttermRequest") val shorttermRequest: IllDaysShortTermRequestDTO? = null,
)

@Serializable
data class IllDaysShortTermRequestDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("request") val request: IllDaysRequestPlaceholderDTO? = null,
    @SerialName("requestFileList") val requestFileList: List<IllDaysRequestFileDTO>? = null,
    @SerialName("requestHelpType") val requestHelpType: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("serviceDateTimeStamp") val serviceDateTimeStamp: Long? = null,
)

@Serializable
data class IllDaysRequestPlaceholderDTO(
    @SerialName("id") val id: String? = null,
)

@Serializable
data class IllDaysRequestFileDTO(
    @SerialName("documentFile") val documentFile: String? = null,
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("editDate") val editDate: String? = null,
    @SerialName("editUser") val editUser: String? = null,
    @SerialName("id") val id: String? = null,
)
