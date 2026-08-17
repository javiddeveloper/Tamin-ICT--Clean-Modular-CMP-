package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.util.toJalaliParts
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

