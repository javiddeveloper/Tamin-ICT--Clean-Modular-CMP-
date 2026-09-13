package com.tamin.taminhamrah.model.weddingPresent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ShortTermMarriageRequestDTO(
    @SerialName("partnerNationalId") val partnerNationalId: String,
    @SerialName("shorttermRequest") val shortTermRequest: MarriageGiftRequestDTO,
    @SerialName("weddingDateTimeStamp") val weddingDateTimeStamp: Long,
)

@Serializable
data class MarriageGiftRequestDTO(
    @SerialName("request") val request: WeddingPresentRequestDTO? = null,
    @SerialName("requestFileList") val requestFileList: List<JsonElement>? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("requestHelpType") val requestHelpType: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("serviceDateTimeStamp") val serviceDateTimeStamp: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
)
