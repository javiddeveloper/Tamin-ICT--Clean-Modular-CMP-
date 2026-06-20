package com.tamin.taminhamrah.model.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TalfighInfoDTO(
    @SerialName("list") val list: List<TalfighInfoItemDTO>?,
    @SerialName("total") val total: Int?
)

@Serializable
data class TalfighInfoItemDTO(
    @SerialName("hisMonth8") val hisMonth8: String?,
    @SerialName("hisMonth9") val hisMonth9: String?,
    @SerialName("hisMonth6") val hisMonth6: String?,
    @SerialName("hisMonth7") val hisMonth7: String?,
    @SerialName("hisMonth1") val hisMonth1: String?,
    @SerialName("hisMonth4") val hisMonth4: String?,
    @SerialName("hisMonth5") val hisMonth5: String?,
    @SerialName("hisMonth2") val hisMonth2: String?,
    @SerialName("hisMonth3") val hisMonth3: String?,
    @SerialName("hisMonth10") val hisMonth10: String?,
    @SerialName("risuid") val risuid: String?,
    @SerialName("hisMonth11") val hisMonth11: String?,
    @SerialName("hisMonth12") val hisMonth12: String?,
    @SerialName("historyYears") val historyYears: Int?,
    @SerialName("historyMonths") val historyMonths: Int?,
    @SerialName("sumYear") val sumYear: Int?,
    @SerialName("historyDays") val historyDays: Int?,
    @SerialName("sumHistoryYears") val sumHistoryYears: Int?,
    @SerialName("id") val id: Int?,
    @SerialName("hisYear") val hisYear: String?
)
