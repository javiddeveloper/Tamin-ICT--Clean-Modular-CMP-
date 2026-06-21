package com.tamin.taminhamrah.model.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TalfighInfoDTO(
    @SerialName("list") val list: List<TalfighInfoItemDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class TalfighInfoItemDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("hisMonth1") val hisMonth1: String? = null,
    @SerialName("hisMonth10") val hisMonth10: String? = null,
    @SerialName("hisMonth11") val hisMonth11: String? = null,
    @SerialName("hisMonth12") val hisMonth12: String? = null,
    @SerialName("hisMonth2") val hisMonth2: String? = null,
    @SerialName("hisMonth3") val hisMonth3: String? = null,
    @SerialName("hisMonth4") val hisMonth4: String? = null,
    @SerialName("hisMonth5") val hisMonth5: String? = null,
    @SerialName("hisMonth6") val hisMonth6: String? = null,
    @SerialName("hisMonth7") val hisMonth7: String? = null,
    @SerialName("hisMonth8") val hisMonth8: String? = null,
    @SerialName("hisMonth9") val hisMonth9: String? = null,
    @SerialName("hisYear") val hisYear: String? = null,
    @SerialName("historyDays") val historyDays: Int? = null,
    @SerialName("historyMonths") val historyMonths: Int? = null,
    @SerialName("historyYears") val historyYears: Int? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("sumHistoryYears") val sumHistoryYears: Int? = null,
    @SerialName("sumYear") val sumYear: Int? = null,
)
