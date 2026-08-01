package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.util.toPersianDigits
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringArrayResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.jalali_months

/**
 * The month names, resolved once per composition.
 *
 * Keyed on the resolved list so the same [ImmutableList] instance comes back every time: the
 * grouping below runs inside a remember that takes it as a key, and a fresh list per
 * recomposition would regroup the whole timeline on every frame.
 */
@Composable
fun rememberJalaliMonthNames(): ImmutableList<String> {
    val names = stringArrayResource(Res.array.jalali_months)
    return remember(names) { names.toImmutableList() }
}

/**
 * Turns a `1404/02/15` prescription date into «اردیبهشت ۱۴۰۴» for the timeline's group header.
 *
 * Takes [monthNames] rather than reading them itself: this runs inside a remember block, where
 * composition — and therefore a resource lookup — is not available. Use [rememberJalaliMonthNames].
 *
 * Falls back to the date itself when the shape is not recognized, so an unexpected value still
 * groups under something readable instead of disappearing.
 */
fun String.toJalaliMonthLabel(monthNames: List<String>): String {
    val parts = toJalaliParts() ?: return toPersianDigits()
    val (year, month, _) = parts
    if (month !in 1..monthNames.size) return toPersianDigits()
    return "${monthNames[month - 1]} ${year.toString().toPersianDigits()}"
}

/**
 * Renders a record date as `۱۴۰۵/۰۱/۳۱`.
 *
 * The endpoint returns dates unseparated (`14050131`), so displaying the raw value gives an
 * unreadable run of digits. Values that already carry separators are passed through.
 */
fun String.toJalaliDateLabel(): String {
    val parts = toJalaliParts() ?: return toPersianDigits()
    val (year, month, day) = parts
    val monthText = month.toString().padStart(2, '0')
    val dayText = day.toString().padStart(2, '0')
    return "$year/$monthText/$dayText".toPersianDigits()
}

/**
 * Splits a record date into year/month/day.
 *
 * Handles both shapes seen from the API: `1404/02/15` and the unseparated `14050131`.
 */
private fun String.toJalaliParts(): Triple<Int, Int, Int>? {
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
