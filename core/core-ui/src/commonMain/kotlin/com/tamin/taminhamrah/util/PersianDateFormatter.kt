package com.tamin.taminhamrah.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object PersianDateFormatter {

    fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return ""
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val (jy, jm, jd) = gregorianToJalali(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth)
        return "${jy.toPersianDigits()}/${jm.toTwoDigitPersian()}/${jd.toTwoDigitPersian()}"
    }

    private fun Int.toTwoDigitPersian(): String {
        return toString().padStart(2, '0').toPersianDigits()
    }

    private fun Int.toPersianDigits(): String {
        return toString().toPersianDigits()
    }

    private fun String.toPersianDigits(): String {
        return map { char ->
            when (char) {
                '0' -> '۰'
                '1' -> '۱'
                '2' -> '۲'
                '3' -> '۳'
                '4' -> '۴'
                '5' -> '۵'
                '6' -> '۶'
                '7' -> '۷'
                '8' -> '۸'
                '9' -> '۹'
                else -> char
            }
        }.joinToString("")
    }

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + gDaysInMonth[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 979 * (days / 36524)
        days %= 36524
        if (days >= 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm = if (days < 186) 1 + days / 31 else 7 + (days - 186) / 30
        val jd = 1 + if (days < 186) days % 31 else (days - 186) % 30
        return Triple(jy, jm, jd)
    }
}
