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
import com.tamin.taminhamrah.model.history.WageDetailDN
import com.tamin.taminhamrah.model.history.WageDetailPR

fun TalfighInfoItemDN.toPresentation(): TalfighInfoItemPR {
    return TalfighInfoItemPR(
        months = this.months.map { it ?: "" },
        risuid = risuid ?: "",
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

fun WageDetailDN.toPresentation() = WageDetailPR(
    month = month ?: "",
    wage = wage ?: ""
)

fun DastmozdInfoItemDN.toPresentation(): DastmozdInfoItemPR {
    return DastmozdInfoItemPR(
        wageDetails = this.wageDetails.map { it.toPresentation() },
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
