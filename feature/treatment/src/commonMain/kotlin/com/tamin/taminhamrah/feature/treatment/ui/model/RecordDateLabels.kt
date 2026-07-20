package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.util.toPersianDigits

private val JALALI_MONTHS = listOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
)

/**
 * Turns a `1404/02/15` prescription date into «اردیبهشت ۱۴۰۴» for the timeline's group header.
 *
 * Falls back to the date itself when the shape is not recognized, so an unexpected value still
 * groups under something readable instead of disappearing.
 */
fun String.toJalaliMonthLabel(): String {
    val parts = split("/")
    val year = parts.getOrNull(0)?.toIntOrNull()
    val month = parts.getOrNull(1)?.toIntOrNull()
    if (year == null || month == null || month !in 1..JALALI_MONTHS.size) return toPersianDigits()
    return "${JALALI_MONTHS[month - 1]} ${year.toString().toPersianDigits()}"
}
