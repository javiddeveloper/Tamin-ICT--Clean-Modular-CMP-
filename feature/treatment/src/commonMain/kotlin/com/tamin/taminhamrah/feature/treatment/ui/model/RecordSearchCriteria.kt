package com.tamin.taminhamrah.feature.treatment.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPR
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPricePR
import com.tamin.taminhamrah.util.currentTimeMillis
import com.tamin.taminhamrah.util.getOneMonthAgoTimestamp

/**
 * What the advanced search asks for.
 *
 * Only [tab] and the date bounds are query parameters — the patient-history endpoint filters on
 * type and a date range and nothing else. [nameQuery] and the cost bounds are applied to the
 * returned list by [matches], so they narrow what was fetched rather than what is fetched.
 */
@Immutable
data class RecordSearchCriteria(
    val tab: RecordTab = RecordTab.Default,
    /** Epoch millis, or null to fall back to the selected period. */
    val startDate: String? = null,
    val endDate: String? = null,
    /** Matched against the prescriber and the center. */
    val nameQuery: String = "",
    val minAmount: String = "",
    val maxAmount: String = "",
) {
    /** Whether anything beyond the defaults was asked for, so the screen can show it is filtering. */
    val isActive: Boolean
        get() = startDate != null || nameQuery.isNotBlank() || filtersOnAmount

    /** Whether a cost bound was given, which is what makes the per-record price lookup worth doing. */
    val filtersOnAmount: Boolean
        get() = minAmount.toAmountOrNull() != null || maxAmount.toAmountOrNull() != null

    /**
     * The range to query, with a half-set one completed.
     *
     * Picking only one end still means a range, so the missing end is filled rather than the whole
     * range being dropped: no «تا» means up to today, no «از» means from a month back. Returns null
     * only when neither end was given, which hands the query back to the period preset.
     */
    fun resolvedRange(): Pair<String, String>? = when {
        startDate != null && endDate != null -> startDate to endDate
        startDate != null -> startDate to currentTimeMillis().toString()
        endDate != null -> getOneMonthAgoTimestamp() to endDate
        else -> null
    }

    /**
     * Whether a record survives the client-side part of the search.
     *
     * [prices] is the per-record price breakdown, fetched separately because the list endpoint
     * carries no amount; the cost filter compares against «سهم شما» (`headSsoPayment`). Pass an
     * empty map when the cost bounds are not in use.
     */
    fun matches(
        record: ElectronicPrescriptionPR,
        prices: Map<String, ElectronicPrescriptionPricePR> = emptyMap(),
    ): Boolean = matchesName(record) && matchesAmount(record, prices)

    private fun matchesName(record: ElectronicPrescriptionPR): Boolean {
        if (nameQuery.isBlank()) return true
        val needle = nameQuery.trim()
        return record.docName.contains(needle, ignoreCase = true) ||
            record.location.contains(needle, ignoreCase = true) ||
            record.specDesc.contains(needle, ignoreCase = true)
    }

    /**
     * A record is kept when its insured share falls inside the bounds.
     *
     * A record whose price has not arrived is kept rather than hidden: the lookup is per record
     * and can fail, and silently dropping records would misrepresent the person's history.
     */
    private fun matchesAmount(
        record: ElectronicPrescriptionPR,
        prices: Map<String, ElectronicPrescriptionPricePR>,
    ): Boolean {
        if (!filtersOnAmount) return true
        val amount = prices[record.noteHeadEprescID]?.headSsoPayment?.toLongOrNull() ?: return true
        val min = minAmount.toAmountOrNull()
        val max = maxAmount.toAmountOrNull()
        return (min == null || amount >= min) && (max == null || amount <= max)
    }
}

/** Reads a typed amount, tolerating Persian digits and thousands separators. */
private fun String.toAmountOrNull(): Long? {
    if (isBlank()) return null
    val normalised = buildString {
        this@toAmountOrNull.forEach { char ->
            when {
                char.isDigit() -> append(char)
                char in PERSIAN_DIGITS -> append(PERSIAN_DIGITS.indexOf(char))
                else -> Unit
            }
        }
    }
    return normalised.toLongOrNull()
}

private const val PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹"
