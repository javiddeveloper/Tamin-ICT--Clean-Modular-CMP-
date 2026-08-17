package com.tamin.taminhamrah.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val PERSIAN_ZERO = '۰'

/**
 * Converts the ASCII digits in this string to Persian-Indic digits, leaving every
 * other character untouched. Shared by the date and price formatters so both render
 * numerals the same way.
 */
fun String.toPersianDigits(): String = map { char ->
    if (char in '0'..'9') PERSIAN_ZERO + (char - '0') else char
}.joinToString("")

fun String.toFormattedDate(): String =
    try {
        if (length == 8) {
            "${substring(0, 4)}/${substring(4, 6)}/${substring(6, 8)}"
        } else {
            this
        }
    } catch (e: Exception) {
        this
    }

/**
 * Converts a Persian date string (possibly with slashes and Persian digits)
 * to a clean ASCII "yyyyMMdd" format for API consumption.
 */
fun String.toApiDateFormat(): String = this
    .replace("/", "")
    .map { char ->
        if (char in PERSIAN_ZERO..PERSIAN_ZERO + 9) '0' + (char - PERSIAN_ZERO) else char
    }.joinToString("")

fun currentTime(): String {
    val now = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val time = "${now.hour.toString().padStart(2, '0')}:" +
        now.minute.toString().padStart(2, '0')

    return time.toPersianDigits()
}

/**
 * Renders a Jalali date as `۱۴۰۵/۰۱/۳۱`.
 *
 * Several endpoints return dates unseparated (`14050131`, confirmed against a live
 * `commission-confrimation` payload), so showing the raw value gives an unreadable run of digits.
 * Values that already carry separators, and values that are a placeholder rather than a date, are
 * passed through with only their digits converted.
 */
fun String.toJalaliDateLabel(): String {
    val parts = toJalaliParts() ?: return toPersianDigits()
    val (year, month, day) = parts
    val monthText = month.toString().padStart(2, '0')
    val dayText = day.toString().padStart(2, '0')
    return "$year/$monthText/$dayText".toPersianDigits()
}

/**
 * Splits a Jalali date into year/month/day.
 *
 * Handles both shapes the API sends: `1404/02/15` and the unseparated `14050131`.
 */
fun String.toJalaliParts(): Triple<Int, Int, Int>? {
    val digits = filter { it.isDigit() }
    return when {
        contains("/") -> {
            val parts = split("/")
            val y = parts.getOrNull(0)?.toIntOrNull() ?: return null
            val m = parts.getOrNull(1)?.toIntOrNull() ?: return null
            val d = parts.getOrNull(2)?.toIntOrNull() ?: 1
            Triple(y, m, d)
        }

        digits.length == COMPACT_DATE_LENGTH -> Triple(
            digits.substring(0, 4).toInt(),
            digits.substring(4, 6).toInt(),
            digits.substring(6, 8).toInt(),
        )

        else -> null
    }
}

private const val COMPACT_DATE_LENGTH = 8

object PersianDateFormatter {

    fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return ""
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val (jy, jm, jd) = gregorianToJalali(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth)
        return "${jy.toPersianDigits()}/${jm.toTwoDigitPersian()}/${jd.toTwoDigitPersian()}"
    }

    /**
     * The current year in the Jalali calendar. Used for labeling current-year totals,
     * which would otherwise need a hardcoded year that silently goes stale.
     */
    fun currentJalaliYear(): Int {
        val dateTime = Instant.fromEpochMilliseconds(currentTimeMillis())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return gregorianToJalali(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth).first
    }

    /** Jalali month names, index 0 = فروردین. */
    val monthNames: List<String> = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
    )

    /** Today, as a Jalali year/month/day. */
    fun today(): Triple<Int, Int, Int> {
        val dateTime = Instant.fromEpochMilliseconds(currentTimeMillis())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return gregorianToJalali(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth)
    }

    /** Days in a Jalali month: 31 for the first six, 30 for the next five, 29/30 for اسفند. */
    fun daysInMonth(jy: Int, jm: Int): Int = when {
        jm <= 6 -> 31
        jm <= 11 -> 30
        isLeapYear(jy) -> 30
        else -> 29
    }

    /**
     * Which weekday a Jalali month starts on, as 0 = شنبه … 6 = جمعه.
     *
     * The grid needs this to indent the first row; kotlinx's [LocalDate.dayOfWeek] is Monday-based,
     * so it is shifted to put شنبه first.
     */
    fun firstWeekdayOfMonth(jy: Int, jm: Int): Int {
        val (gy, gm, gd) = jalaliToGregorian(jy, jm, 1)
        val isoDayNumber = LocalDate(gy, gm, gd).dayOfWeek.isoDayNumber
        return (isoDayNumber + 1) % 7
    }

    /**
     * Midnight of a Jalali date, in epoch milliseconds, for the date-range endpoints.
     *
     * Uses the device's zone, which is what the range filters want. For anything the service
     * stores as a calendar day, use [toEpochMillisUtc] instead.
     */
    fun toEpochMillis(jy: Int, jm: Int, jd: Int): Long =
        startOfDay(jy, jm, jd, TimeZone.currentSystemDefault())

    private fun startOfDay(jy: Int, jm: Int, jd: Int, timeZone: TimeZone): Long {
        val (gy, gm, gd) = jalaliToGregorian(jy, jm, jd)
        return LocalDate(gy, gm, gd)
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()
    }

    /**
     * Midnight UTC of a Jalali date, for services that record a calendar day rather than an instant.
     *
     * Its own function so callers need no kotlinx-datetime dependency of their own, and so the
     * reason is stated once: Tehran midnight is 20:30 UTC the previous day, which files a date one
     * day early. The accounts endpoint returns exactly midnight UTC for the rows it already holds.
     */
    fun toEpochMillisUtc(jy: Int, jm: Int, jd: Int): Long =
        startOfDay(jy, jm, jd, TimeZone.UTC)

    /**
     * Whole days from [date] to today, or null when [date] is not a Jalali date.
     *
     * Both shapes the services send are accepted (`14050131` and `1405/01/31`). Used for the
     * filing deadlines that are decided on the device rather than by a `diff-days` endpoint; a
     * negative result means the date is in the future.
     */
    fun daysSince(date: String): Int? {
        val (year, month, day) = date.toJalaliParts() ?: return null
        val (todayYear, todayMonth, todayDay) = today()
        return dayNumber(todayYear, todayMonth, todayDay) - dayNumber(year, month, day)
    }

    /** Formats a Jalali date the way the API and the UI both spell it: `1404/02/15`. */
    fun format(jy: Int, jm: Int, jd: Int): String =
        "${jy.toPersianDigits()}/${jm.toTwoDigitPersian()}/${jd.toTwoDigitPersian()}"

    /**
     * Whether اسفند has 30 days, derived from the conversion's own day count rather than a
     * separate cycle rule: two independent leap rules drift apart, and the one that disagreed put
     * a 30th of اسفند on the 1st of فروردین.
     */
    private fun isLeapYear(jy: Int): Boolean =
        dayNumber(jy + 1, 1, 1) - dayNumber(jy, 1, 1) == DAYS_IN_LEAP_YEAR

    /** Days elapsed since the Jalali epoch, the quantity [jalaliToGregorian] is built on. */
    private fun dayNumber(jy: Int, jm: Int, jd: Int): Int {
        val jy1 = jy - 979
        val jm1 = jm - 1
        return 365 * jy1 + (jy1 / 33) * 8 + ((jy1 % 33) + 3) / 4 +
            (if (jm1 < 6) 31 * jm1 else 186 + 30 * (jm1 - 6)) + (jd - 1)
    }

    /** Inverse of [gregorianToJalali], using the same day-count arithmetic. */
    private fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val dayCount = dayNumber(jy, jm, jd)
        // 78 pairs with the 355660 above; the two directions must agree or a picked date comes
        // back a day different from what was tapped.
        var gDayNo = dayCount + 78
        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097
        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) gDayNo++ else leap = false
        }
        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461
        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }
        val monthLengths = intArrayOf(
            31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31,
        )
        var gm = 0
        while (gm < 12 && gDayNo >= monthLengths[gm]) {
            gDayNo -= monthLengths[gm]
            gm++
        }
        return Triple(gy, gm + 1, gDayNo + 1)
    }

    private fun Int.toTwoDigitPersian(): String {
        return toString().padStart(2, '0').toPersianDigits()
    }

    private fun Int.toPersianDigits(): String {
        return toString().toPersianDigits()
    }

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        // 355660, not 355666: the larger constant shifted every converted date six days late, so
        // Nowruz 1404 (2025-03-20) formatted as 1404/01/07. Verified against four known Nowruz
        // dates in PersianDateFormatterTest.
        var days = 355660 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + gDaysInMonth[gm - 1]
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

    private const val DAYS_IN_LEAP_YEAR = 366
}
