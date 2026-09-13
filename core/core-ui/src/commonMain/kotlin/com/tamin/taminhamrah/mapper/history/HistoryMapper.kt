package com.tamin.taminhamrah.mapper.history

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.DastmozdInfoPR
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemPR
import com.tamin.taminhamrah.model.history.HistoryJobInfoPR
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.UserInfoPR
import com.tamin.taminhamrah.model.history.TalfighInfoItemPR
import com.tamin.taminhamrah.model.history.TalfighInfoPR
import com.tamin.taminhamrah.model.history.WageDetailDN
import com.tamin.taminhamrah.model.history.WageDetailPR
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.send_history_type_all
import taminx.core.core_ui.send_history_type_combined
import taminx.core.core_ui.send_history_type_wages
import kotlin.jvm.JvmName

/**
 * What each «سوابق» certificate is called on screen.
 *
 * One table for a type that is now read in two places — the اعلام سابقه wizard picks these to send,
 * «مجموع سوابق» picks the same three to download — so the wording cannot drift between them.
 */
fun HistoryCertificateType.labelRes(): StringResource = when (this) {
    HistoryCertificateType.ALL -> Res.string.send_history_type_all
    HistoryCertificateType.WAGES -> Res.string.send_history_type_wages
    HistoryCertificateType.COMBINED -> Res.string.send_history_type_combined
}

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

fun UserInfoDN.toPresentation(): UserInfoPR {
    return UserInfoPR(
        serial1 = serial1 ?: "",
        militaryServiceCode = militaryServiceCode ?: "",
        fatherName = fatherName ?: "",
        lastName = lastName ?: "",
        serial2 = serial2 ?: "",
        creationTime = creationTime ?: 0L,
        lastModificationTime = lastModificationTime ?: 0L,
        cityCode = cityCode ?: "",
        socialSecurityNumber = socialSecurityNumber ?: "",
        lastModifiedBy = lastModifiedBy ?: "",
        issueplaceName = issueplaceName ?: "",
        birthDate = birthDate ?: "",
        firstName = firstName ?: "",
        insuranceNumber = insuranceNumber ?: "",
        genderCode = genderCode ?: "",
        nationalID = nationalID ?: "",
        marriageCode = marriageCode ?: "",
        createdBy = createdBy ?: "",
        identityNumber = identityNumber ?: "",
        countryCode = countryCode ?: "",
        id = id ?: "",
        birthDateTimestamp = birthDateTimestamp ?: 0L,
        issueplace = issueplace ?: "",
        nationCode = nationCode ?: ""
    )
}

fun HistoryJobInfoItemDN.toPresentation(): HistoryJobInfoItemPR {
    return HistoryJobInfoItemPR(
        risuid = risuid ?: "",
        rwshName = rwshName ?: "",
        brhcode = brhcode ?: "",
        id = id ?: 0,
        jobDesc = jobDesc ?: "",
        startDate = startDate ?: "",
        rwshId = rwshId ?: ""
    )
}

@JvmName("toPresentationHistoryJobInfo")
fun List<HistoryJobInfoItemDN>.toPresentation(): List<HistoryJobInfoItemPR> {
    return this.map { it.toPresentation() }
}

fun HistoryJobInfoDN.toPresentation(): HistoryJobInfoPR {
    return HistoryJobInfoPR(
        list = list?.toPresentation() ?: emptyList(),
        total = total ?: 0
    )
}
