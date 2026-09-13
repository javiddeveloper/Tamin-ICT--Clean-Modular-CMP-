package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.HistoryWageRowEntity
import com.tamin.taminhamrah.data.local.entity.HistoryYearEntity
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.WageDetailDN

/**
 * The delimiter the cached month and wage lists are joined on.
 *
 * A comma is safe here because every value is a number the service sent as a string; nothing in
 * these two columns can contain one.
 */
private const val SEPARATOR = ","

/** Months are always twelve, so a short or absent cached string is padded rather than trusted. */
private const val MONTHS = 12

private fun String.toMonthList(): List<String> =
    split(SEPARATOR).let { parts -> List(MONTHS) { parts.getOrNull(it).orEmpty() } }

fun TalfighInfoItemDN.toEntity(position: Int) = HistoryYearEntity(
    hisYear = hisYear.orEmpty(),
    months = months.joinToString(SEPARATOR) { it.orEmpty() },
    historyYears = historyYears ?: 0,
    historyMonths = historyMonths ?: 0,
    historyDays = historyDays ?: 0,
    sumHistoryYears = sumHistoryYears ?: 0,
    sumYear = sumYear ?: 0,
    position = position,
)

fun HistoryYearEntity.toDomain() = TalfighInfoItemDN(
    months = months.toMonthList(),
    risuid = null,
    historyYears = historyYears,
    historyMonths = historyMonths,
    sumYear = sumYear,
    historyDays = historyDays,
    sumHistoryYears = sumHistoryYears,
    id = position,
    hisYear = hisYear,
)

fun DastmozdInfoItemDN.toEntity(position: Int) = HistoryWageRowEntity(
    id = id ?: position,
    hisYear = hisyear.orEmpty(),
    months = wageDetails.joinToString(SEPARATOR) { it.month.orEmpty() },
    wages = wageDetails.joinToString(SEPARATOR) { it.wage.orEmpty() },
    workshopName = rwshname.orEmpty(),
    branchName = brhname.orEmpty(),
    historyTypeDesc = historytypedesc.orEmpty(),
    workshopId = rwshid.orEmpty(),
    position = position,
)

fun HistoryWageRowEntity.toDomain(): DastmozdInfoItemDN {
    val monthList = months.toMonthList()
    val wageList = wages.toMonthList()
    return DastmozdInfoItemDN(
        wageDetails = List(MONTHS) { WageDetailDN(month = monthList[it], wage = wageList[it]) },
        hisyear = hisYear,
        id = id,
        risufname = null,
        risubirthdate = null,
        risuidserial2 = null,
        risuidserial1 = null,
        rwshname = workshopName,
        expcitycode = null,
        brhcode = null,
        risuidno = null,
        risudname = null,
        risuid = null,
        risulname = null,
        risunatcode = null,
        brhname = branchName,
        historytypedesc = historyTypeDesc,
        rwshid = workshopId,
    )
}
