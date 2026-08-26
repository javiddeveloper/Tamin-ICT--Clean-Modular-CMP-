package com.tamin.taminhamrah.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

private const val PERSIAN_ZERO = '۰'

/**
 * Converts the ASCII digits in this string to Persian-Indic digits (`U+06F0`–`U+06F9`),
 * leaving every other character untouched. Shared by the date and price formatters so
 * both produce numerals the same way.
 *
 * Use this when the characters themselves must be Persian — share/copy payloads,
 * notifications, and any surface that does not inherit
 * [com.tamin.taminhamrah.ui.theme.taminHamrahTypography].
 * Compose UI that uses the theme typography already paints ASCII digits as Persian
 * via Vazirmatn `ss01`; converting here as well is harmless but not required.
 *
 * See `DEFAULT_FONT_FEATURES` in `Type.kt` for the render-time mechanism.
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

    /** The current wall-clock time as hour/minute, for time pickers' default selection. */
    fun now(): Pair<Int, Int> {
        val dateTime = Instant.fromEpochMilliseconds(currentTimeMillis())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return dateTime.hour to dateTime.minute
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
     * Whole calendar days between two epoch-millis instants in the device's zone, for range/
     * deadline checks (e.g. "end date must be at least N days before today"). Converts through
     * [LocalDate] rather than dividing the millis difference so a DST transition between the two
     * instants can't shift the count by a day.
     */
    fun daysBetween(startMillis: Long, endMillis: Long): Int {
        val start = Instant.fromEpochMilliseconds(startMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val end = Instant.fromEpochMilliseconds(endMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
        return start.daysUntil(end)
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
        val monthIndex = jm - 1
        return yearStartDayNumber(jy - JALALI_EPOCH_YEAR) +
            (if (monthIndex < 6) 31 * monthIndex else FIRST_HALF_DAYS + 30 * (monthIndex - 6)) +
            (jd - 1)
    }

    /**
     * Both directions run through [dayNumber], so they are inverses by construction.
     *
     * They used to be two independent pieces of arithmetic with two different leap rules, which
     * disagreed around 1403's Nowruz — one placed 2024-03-20 in 1403, the other in 1402. The
     * Gregorian side is now `kotlinx.datetime`'s proleptic calendar rather than hand-rolled
     * 146097/36524/1461 cycle stepping, leaving exactly one leap rule in this file: [dayNumber]'s.
     */
    internal fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val date = LocalDate.fromEpochDays(
            GREGORIAN_ANCHOR_EPOCH_DAYS + dayNumber(jy, jm, jd) + JALALI_GREGORIAN_DAY_OFFSET,
        )
        return Triple(date.year, date.monthNumber, date.dayOfMonth)
    }

    private fun Int.toTwoDigitPersian(): String {
        return toString().padStart(2, '0').toPersianDigits()
    }

    private fun Int.toPersianDigits(): String {
        return toString().toPersianDigits()
    }

    /** The exact inverse of [jalaliToGregorian]. */
    internal fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val dayCount = LocalDate(gy, gm, gd).toEpochDays() -
            GREGORIAN_ANCHOR_EPOCH_DAYS - JALALI_GREGORIAN_DAY_OFFSET
        return fromDayNumber(dayCount)
    }

    /** Inverse of [dayNumber]: which Jalali date sits [dayCount] days after 979/01/01. */
    private fun fromDayNumber(dayCount: Int): Triple<Int, Int, Int> {
        // Years are 365 or 366 days, so dividing by 366 never overshoots; step up from there.
        var yearsSinceEpoch = dayCount / 366
        while (yearStartDayNumber(yearsSinceEpoch + 1) <= dayCount) yearsSinceEpoch++

        val dayOfYear = dayCount - yearStartDayNumber(yearsSinceEpoch)
        val month =
            if (dayOfYear < FIRST_HALF_DAYS) 1 + dayOfYear / 31
            else 7 + (dayOfYear - FIRST_HALF_DAYS) / 30
        val day =
            1 + if (dayOfYear < FIRST_HALF_DAYS) dayOfYear % 31
            else (dayOfYear - FIRST_HALF_DAYS) % 30
        return Triple(yearsSinceEpoch + JALALI_EPOCH_YEAR, month, day)
    }

    /** Days from 979/01/01 to the first of فروردین [yearsSinceEpoch] years later. */
    private fun yearStartDayNumber(yearsSinceEpoch: Int): Int =
        365 * yearsSinceEpoch + (yearsSinceEpoch / 33) * 8 + ((yearsSinceEpoch % 33) + 3) / 4

    /** فروردین through شهریور, the six 31-day months the month arithmetic splits on. */
    private const val FIRST_HALF_DAYS = 186

    /** [dayNumber] counts from the first of فروردین 979. */
    private const val JALALI_EPOCH_YEAR = 979

    /**
     * Where the two calendars are pinned to each other. Verified against real Nowruz dates in
     * JalaliConversionTest — retuning this shifts every converted date, in both directions.
     */
    private const val JALALI_GREGORIAN_DAY_OFFSET = 79
    private val GREGORIAN_ANCHOR_EPOCH_DAYS = LocalDate(1600, 1, 1).toEpochDays()

    private const val DAYS_IN_LEAP_YEAR = 366
}
