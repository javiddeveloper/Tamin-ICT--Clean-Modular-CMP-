package com.tamin.taminhamrah.mapper.history

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.DastmozdInfoPR
import kotlin.jvm.JvmName
import com.tamin.taminhamrah.model.history.TalfighInfoItemPR
import com.tamin.taminhamrah.model.history.TalfighInfoPR
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN

fun TalfighInfoItemDN.toPresentation(): TalfighInfoItemPR {
    return TalfighInfoItemPR(
        hisMonth8 = hisMonth8 ?: "",
        hisMonth9 = hisMonth9 ?: "",
        hisMonth6 = hisMonth6 ?: "",
        hisMonth7 = hisMonth7 ?: "",
        hisMonth1 = hisMonth1 ?: "",
        hisMonth4 = hisMonth4 ?: "",
        hisMonth5 = hisMonth5 ?: "",
        hisMonth2 = hisMonth2 ?: "",
        hisMonth3 = hisMonth3 ?: "",
        hisMonth10 = hisMonth10 ?: "",
        risuid = risuid ?: "",
        hisMonth11 = hisMonth11 ?: "",
        hisMonth12 = hisMonth12 ?: "",
        historyYears = historyYears ?: 0,
        historyMonths = historyMonths ?: 0,
        sumYear = sumYear ?: 0,
        historyDays = historyDays ?: 0,
        sumHistoryYears = sumHistoryYears ?: 0,
        id = id ?: 0,
        hisYear = hisYear ?: ""
    )
}

@JvmName("toPresentationTalfighInfo")
fun List<TalfighInfoItemDN>.toPresentation(): List<TalfighInfoItemPR> {
    return this.map { it.toPresentation() }
}

fun TalfighInfoDN.toPresentation(): TalfighInfoPR {
    return TalfighInfoPR(
        list = list?.toPresentation() ?: emptyList(),
        total = total ?: 0
    )
}

fun DastmozdInfoItemDN.toPresentation(): DastmozdInfoItemPR {
    return DastmozdInfoItemPR(
        hismon1 = hismon1 ?: "",
        hismon2 = hismon2 ?: "",
        hismon3 = hismon3 ?: "",
        hismon4 = hismon4 ?: "",
        hismon5 = hismon5 ?: "",
        hismon6 = hismon6 ?: "",
        hismon7 = hismon7 ?: "",
        hismon8 = hismon8 ?: "",
        hismon9 = hismon9 ?: "",
        hismon10 = hismon10 ?: "",
        hismon11 = hismon11 ?: "",
        hismon12 = hismon12 ?: "",
        hiswage1 = hiswage1 ?: "",
        hiswage2 = hiswage2 ?: "",
        hiswage3 = hiswage3 ?: "",
        hiswage4 = hiswage4 ?: "",
        hiswage5 = hiswage5 ?: "",
        hiswage6 = hiswage6 ?: "",
        hiswage7 = hiswage7 ?: "",
        hiswage8 = hiswage8 ?: "",
        hiswage9 = hiswage9 ?: "",
        hiswage10 = hiswage10 ?: "",
        hiswage11 = hiswage11 ?: "",
        hiswage12 = hiswage12 ?: "",
        hisyear = hisyear ?: "",
        id = id ?: 0,
        risufname = risufname ?: "",
        risubirthdate = risubirthdate ?: "",
        risuidserial2 = risuidserial2 ?: "",
        risuidserial1 = risuidserial1 ?: "",
        rwshname = rwshname ?: "",
        expcitycode = expcitycode ?: "",
        brhcode = brhcode ?: "",
        risuidno = risuidno ?: "",
        risudname = risudname ?: "",
        risuid = risuid ?: "",
        risulname = risulname ?: "",
        risunatcode = risunatcode ?: "",
        brhname = brhname ?: "",
        historytypedesc = historytypedesc ?: "",
        rwshid = rwshid ?: ""
    )
}

@JvmName("toPresentationDastmozdInfo")
fun List<DastmozdInfoItemDN>.toPresentation(): List<DastmozdInfoItemPR> {
    return this.map { it.toPresentation() }
}

fun DastmozdInfoDN.toPresentation(): DastmozdInfoPR {
    return DastmozdInfoPR(
        list = list?.toPresentation() ?: emptyList(),
        total = total ?: 0
    )
}
