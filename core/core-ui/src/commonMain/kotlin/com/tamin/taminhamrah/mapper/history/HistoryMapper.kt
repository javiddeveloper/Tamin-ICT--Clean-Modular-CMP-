package com.tamin.taminhamrah.mapper.history

import kotlin.jvm.JvmName
import com.tamin.taminhamrah.model.history.TalfighInfoItemPR
import com.tamin.taminhamrah.model.history.TalfighInfoPR
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN

fun TalfighInfoItemDN.toPresentation(): TalfighInfoItemPR {
    return TalfighInfoItemPR(
        hisMonth8 = hisMonth8,
        hisMonth9 = hisMonth9,
        hisMonth6 = hisMonth6,
        hisMonth7 = hisMonth7,
        hisMonth1 = hisMonth1,
        hisMonth4 = hisMonth4,
        hisMonth5 = hisMonth5,
        hisMonth2 = hisMonth2,
        hisMonth3 = hisMonth3,
        hisMonth10 = hisMonth10,
        risuid = risuid,
        hisMonth11 = hisMonth11,
        hisMonth12 = hisMonth12,
        historyYears = historyYears,
        historyMonths = historyMonths,
        sumYear = sumYear,
        historyDays = historyDays,
        sumHistoryYears = sumHistoryYears,
        id = id,
        hisYear = hisYear
    )
}

@JvmName("toPresentationTalfighInfo")
fun List<TalfighInfoItemDN>.toPresentation(): List<TalfighInfoItemPR> {
    return this.map { it.toPresentation() }
}

fun TalfighInfoDN.toPresentation(): TalfighInfoPR {
    return TalfighInfoPR(
        list = list.toPresentation(),
        total = total
    )
}
