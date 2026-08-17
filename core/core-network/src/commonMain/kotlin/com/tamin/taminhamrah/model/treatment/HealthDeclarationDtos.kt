package com.tamin.taminhamrah.model.treatment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MedicalAuthoritiesDTO(
    @SerialName("reqHelptype") val supportType: String? = null,
    @SerialName("centerName") val treatmentCenter: String? = null,
    @SerialName("confirmOk") val confirmInBranch: String? = null,
    @SerialName("confirmGet") val confirmStatus: String? = null,
    @SerialName("risuid") val insuranceNumber: String? = null,
    @SerialName("nationalId") val nationalCode: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fromDate") val outpatientRestStartDate: String? = null,
    @SerialName("toDate") val outpatientRestEndDate: String? = null,
    @SerialName("ddSar") val numberOfOutpatientDays: String? = null,
    @SerialName("sDate") val hospitalizationStartDate: String? = null,
    @SerialName("eDate") val hospitalizationEndDate: String? = null,
    @SerialName("ddBas") val numberOfHospitalizationDays: String? = null,
    @SerialName("comment") val description: String? = null,
    @SerialName("branchName") val branch: String? = null,
    @SerialName("fromDateNotConfirm") val fromDateNotConfirm: String? = null,
    @SerialName("toDateNotConfirm") val toDateNotConfirm: String? = null
)
