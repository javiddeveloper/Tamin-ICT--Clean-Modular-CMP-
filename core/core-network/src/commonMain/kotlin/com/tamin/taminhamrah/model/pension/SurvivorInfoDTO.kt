package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurvivorInfo(
    @SerialName("amt20")
    val pensionAfterIncrease: String? = null,
    @SerialName("amt20l")
    val previousPension: String? = null,
    @SerialName("hisaday")
    val originalHistoryDay: String? = null,
    @SerialName("hisamon")
    val originalHistoryMonth: String? = null,
    @SerialName("hisasal")
    val originalHistoryYear: String? = null,
    @SerialName("hisyere")
    val leniencyYear: String? = null,
    @SerialName("hismnte")
    val leniencyMonth: String? = null,
    @SerialName("hisdaye")
    val leniencyDay: String? = null,
    @SerialName("hokmDesc")
    val edictDescription: String? = null,
    val id: String? = null,
    @SerialName("isuType")
    val insuranceType: String? = null,
    val lastName: String? = null,
    val firstName: String? = null,
    @SerialName("mostMot99")
    val firstStageTotalPensionAndProportional: String? = null,
    @SerialName("mot99")
    val firstStageTotalProportional: String? = null,
    @SerialName("amt33")
    val differenceProportionalityBasedHistory: String? = null,
    val nationalCode: String? = null,
    val pensionerId: String? = null,
    val quota: String = "0",
    @SerialName("sumPay")
    val totalAmount: String? = null,
)
