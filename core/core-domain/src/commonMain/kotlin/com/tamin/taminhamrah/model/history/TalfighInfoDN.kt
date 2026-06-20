package com.tamin.taminhamrah.model.history

data class TalfighInfoDN(
    val list: List<TalfighInfoItemDN>?,
    val total: Int?
)

data class TalfighInfoItemDN(
    val hisMonth8: String?,
    val hisMonth9: String?,
    val hisMonth6: String?,
    val hisMonth7: String?,
    val hisMonth1: String?,
    val hisMonth4: String?,
    val hisMonth5: String?,
    val hisMonth2: String?,
    val hisMonth3: String?,
    val hisMonth10: String?,
    val risuid: String?,
    val hisMonth11: String?,
    val hisMonth12: String?,
    val historyYears: Int?,
    val historyMonths: Int?,
    val sumYear: Int?,
    val historyDays: Int?,
    val sumHistoryYears: Int?,
    val id: Int?,
    val hisYear: String?
)
