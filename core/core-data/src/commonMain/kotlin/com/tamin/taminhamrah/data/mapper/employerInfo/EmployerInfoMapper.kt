package com.tamin.taminhamrah.data.mapper.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDTO

fun LegalWorkshopDTO.toDomain(): LegalWorkshopDN =
    LegalWorkshopDN(
        name = name,
        nationalCode = nationalCode,
    )

fun LegalWorkshopCeoDTO.toDomain(): LegalWorkshopCeoDN =
    LegalWorkshopCeoDN(
        firstName = firstName,
        lastName = lastName,
    )

fun LegalWorkshopInfoRequestDN.toDTO(): LegalWorkshopInfoRequestDTO =
    LegalWorkshopInfoRequestDTO(
        birthDate = formatCeoBirthDateForSubmit(ceoBirthDateMillis),
        branchCode = branchCode,
        email = email,
        legalWorkshopTypeCode = legalWorkshopTypeCode,
        mobile = mobile,
        nationalId = ceoNationalId,
        telephon = telephone,
        ticketCode = ticketCode,
        workshopId = workshopId,
        workshopNationalCode = workshopNationalCode,
    )

fun RealWorkshopInfoRequestDN.toDTO(): RealWorkshopInfoRequestDTO =
    RealWorkshopInfoRequestDTO(
        brchcode = branchCode,
        rwshid = workshopCode,
        ticketCode = ticketCode,
    )

private val englishDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val englishMonths = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

private fun epochDayToCivil(epochDay: Long): Triple<Int, Int, Int> {
    val z = epochDay + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = (z - era * 146097).toInt()
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = mp + (if (mp < 10) 3 else -9)
    val year = (y + if (m <= 2) 1 else 0).toInt()
    return Triple(year, m, d)
}

/**
 * Formats epoch millis for the CEO inquiry GET path segment: `"Sun 22 Feb 1987 00:00:00 GMT+0330"`.
 * Uses fixed English abbreviations to prevent locale-dependent formatting bugs.
 */
fun formatCeoBirthDateForInquiry(birthDateMillis: Long): String {
    val epochDay = birthDateMillis / 86400000L
    val (year, month, day) = epochDayToCivil(epochDay)
    val dayOfWeekIndex = (((epochDay + 3) % 7 + 7) % 7).toInt()
    val dayOfWeek = englishDays[dayOfWeekIndex]
    val monthName = englishMonths[month - 1]
    val dayStr = day.toString().padStart(2, '0')
    return "$dayOfWeek $dayStr $monthName $year 00:00:00 GMT+0330"
}

/**
 * Formats epoch millis for the legal workshop submission body: `"1987-02-22T00:00:00.000"`.
 */
fun formatCeoBirthDateForSubmit(birthDateMillis: Long): String {
    val epochDay = birthDateMillis / 86400000L
    val (year, month, day) = epochDayToCivil(epochDay)
    val yearStr = year.toString().padStart(4, '0')
    val monthStr = month.toString().padStart(2, '0')
    val dayStr = day.toString().padStart(2, '0')
    return "$yearStr-$monthStr-${dayStr}T00:00:00.000"
}
