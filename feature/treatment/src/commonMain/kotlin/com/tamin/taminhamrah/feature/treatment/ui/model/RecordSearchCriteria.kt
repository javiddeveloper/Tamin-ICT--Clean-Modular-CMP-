package com.tamin.taminhamrah.feature.treatment.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPR
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPricePR
import com.tamin.taminhamrah.util.containsFoldedWords
import com.tamin.taminhamrah.util.currentTimeMillis
import com.tamin.taminhamrah.util.foldForSearch
import com.tamin.taminhamrah.util.getOneMonthAgoTimestamp
import com.tamin.taminhamrah.util.toFoldedWords

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
    /**
     * A service type picked in the sheet that no tab can express — today only داروخانه (`0`).
     * Set, it replaces [tab] as what the endpoint is asked for; null hands the query back to the tab.
     */
    val prescType: String? = null,
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
        get() = startDate != null || nameQuery.isNotBlank() || filtersOnAmount || prescType != null

    /**
     * The type ids the list request asks for.
     *
     * One place, so the tab row and the sheet can never disagree about what is being queried.
     */
    fun requestTypeIds(): List<String> = prescType?.let(::listOf) ?: tab.requestTypeIds

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

    /**
     * The typed name reduced to the words a record has to contain.
     *
     * Folded once per criteria object rather than once per record: [matches] runs over the whole
     * list, and the query is the same for every row of that pass.
     */
    private val nameWords: List<String> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        nameQuery.toFoldedWords()
    }

    /**
     * Matched on folded text, so a name spelled with Arabic ي/ك, joined by a نیم‌فاصله, or typed
     * with a stray double space still finds its record. See [foldForSearch].
     */
    private fun matchesName(record: ElectronicPrescriptionPR): Boolean {
        val words = nameWords
        if (words.isEmpty()) return true
        return record.docName.containsFoldedWords(words) ||
            record.location.containsFoldedWords(words) ||
            record.specDesc.containsFoldedWords(words)
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
