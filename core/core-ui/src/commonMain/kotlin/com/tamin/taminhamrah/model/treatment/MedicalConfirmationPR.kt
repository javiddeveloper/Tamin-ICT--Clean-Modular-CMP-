package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.ui.NO_DAYS
import com.tamin.taminhamrah.ui.containsAny
import com.tamin.taminhamrah.ui.normalizeArabicLetters
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MedicalConfirmationPR(
    /** Empty whenever the service sends no row identifier, which is currently always. */
    val repId: String,
    val supportType: String,
    val treatmentCenter: String,
    val outpatientRestStartDate: String,
    val outpatientRestEndDate: String,
    val numberOfOutpatientDays: String,
    val inpatientRestStartDate: String,
    val inpatientRestEndDate: String,
    val numberOfInpatientDays: String,
    /**
     * The unapproved period stays as two dates rather than one joined phrase: the word between
     * them is localized, so the card joins them with a string resource.
     */
    val unapprovedFromDate: String,
    val unapprovedToDate: String,
    val branchName: String,
    val branchStatus: String,
    val description: String,
    val statusDesc: String
) {
    /** Both row actions address the row by [repId]; neither can run without one. */
    val hasCertificate: Boolean get() = repId.isNotBlank()

    val hasUnapprovedPeriod: Boolean get() = unapprovedFromDate.isNotBlank() || unapprovedToDate.isNotBlank()

    val hasInpatientRest: Boolean get() = numberOfInpatientDays.isNotBlank() && numberOfInpatientDays != NO_DAYS

    /**
     * A stable key for a lazy list.
     *
     * The service sends no row identifier, so identity has to be the combination that actually
     * tells one verdict from another. Keying on [repId] -- blank on every row -- made every key
     * identical, and `LazyColumn` throws on a duplicate key rather than degrading.
     */
    val listKey: String
        get() = listOf(
            treatmentCenter,
            outpatientRestStartDate,
            outpatientRestEndDate,
            inpatientRestStartDate,
            statusDesc,
        ).joinToString(KEY_SEPARATOR)
}

private const val KEY_SEPARATOR = "|"

/**
 * Where a confirmation stands.
 *
 * One value rather than a pair of loose booleans: `isApproved` and `isPending` could both read
 * true at once, and the list filtered on `!isApproved`, which quietly folded rejected in with
 * pending.
 */
enum class ConfirmationStatus { APPROVED, PENDING, REJECTED, UNKNOWN }

/**
 * Classifies the medical authority's verdict, which arrives only as Persian prose.
 *
 * There is no status *code* on this endpoint -- `confirmGet` is the whole of it -- so the wording
 * has to be read. Two things make that harder than it looks, and both are taken from the wordings
 * the old app enumerates:
 *
 *  - The service spells «تایید» at least three ways («تایید», «تائيد», «تايئد») and mixes Arabic
 *    ي/ك with Persian ی/ک. [normalizeArabicLetters] folds the letter variants away; the remaining
 *    transpositions are listed out.
 *  - «تايئد نشده» and «در انتظار تایید» both *contain* «تایید», so matching approval first marks
 *    every rejected and pending row as approved -- which is exactly what this used to do.
 *    Rejection and pending are therefore tested ahead of approval, and the order is the point.
 */
val MedicalConfirmationPR.confirmationStatus: ConfirmationStatus
    get() {
        val verdict = statusDesc.normalizeArabicLetters()
        return when {
            verdict.isBlank() -> ConfirmationStatus.UNKNOWN
            verdict.containsAny(REJECTED_PHRASES) -> ConfirmationStatus.REJECTED
            verdict.containsAny(PENDING_PHRASES) -> ConfirmationStatus.PENDING
            verdict.containsAny(APPROVED_PHRASES) -> ConfirmationStatus.APPROVED
            else -> ConfirmationStatus.UNKNOWN
        }
    }

// Every phrase below is written post-normalization, so none of them contain ي or ك.
// «قابل بررسی» leads the rejected list because it also contains «بررسی», which marks pending.
private val REJECTED_PHRASES = listOf("قابل بررسی", "نشده", "عدم", "رد شد")
private val PENDING_PHRASES = listOf("انتظار", "بررسی", "ارجاع", "بخشی از دوره", "تکمیل مدارک")
private val APPROVED_PHRASES = listOf("تایید", "تائید", "تایئد", "تأیید")
