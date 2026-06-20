package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EdictInfoDTO(
    @SerialName("pensionerId") val pensionerId: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("idNumber") val idNumber: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("isuType") val insuranceType: String? = null,
    @SerialName("canDateInd") val pensionStartDate: String? = null,
    @SerialName("hisasal") val originalHistoryYear: String? = "0",
    @SerialName("hisamon") val originalHistoryMonth: String? = "0",
    @SerialName("hisaday") val originalHistoryDay: String? = "0",
    @SerialName("hisyere") val additionalYear: String? = "0",
    @SerialName("hismnte") val additionalMonth: String? = "0",
    @SerialName("hisdaye") val additionalDay: String? = "0",
    @SerialName("bexdesc") val basisImplementation: String? = null,
    @SerialName("amt20l") val pensionBeforeIncrease: String? = null,
    @SerialName("amt20") val pensionAfterIncrease: String? = null,
    @SerialName("hokmDesc") val edictDescription: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("mostMot99") val firstStageTotalPensionAndProportional: String? = null,
    @SerialName("mostMot00") val totalPensionBeforeIncrease: String? = null,
    @SerialName("mot99") val firstStageTotalProportional: String? = null,
    @SerialName("sumPay") val totalAmount: String? = null,
    @SerialName("sumPay2") val payableMonthly: String? = null,
    @SerialName("vstramt") val lettersPayableMonthly: String? = null,
)
