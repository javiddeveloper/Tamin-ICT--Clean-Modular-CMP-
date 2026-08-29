package com.tamin.taminhamrah.model.pregnancyPay

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PregnancyMainInfoDTO(
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("genderCode") val genderCode: String? = null,
    @SerialName("serviceDateTimeStamp") val serviceDateTimeStamp: Int? = null,
    @SerialName("branchWorkshop") val branchWorkshop: List<PregnancyBranchWorkshopDTO>? = null,
    @SerialName("bankAccount") val bankAccount: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("insuranceTypeDesc") val insuranceTypeDesc: String? = null,
    @SerialName("insuranceStatusDesc") val insuranceStatusDesc: String? = null,
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier

@Serializable
data class PregnancyBranchWorkshopDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)
