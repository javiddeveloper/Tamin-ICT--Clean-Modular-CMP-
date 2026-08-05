package com.tamin.taminhamrah.model.bankAccount

import androidx.compose.runtime.Immutable

/**
 * One registered account, ready to draw.
 *
 * Every string is final here: grouped, converted to Persian digits and Jalali-formatted by the
 * mapper, so the card does no work per recomposition.
 */
@Immutable
data class BankAccountPR(
    val id: Long,
    val bank: Bank?,
    /** The service's own spelling, shown only when [bank] is null — it is "رفاه کارگران", not a label. */
    val bankNameFallback: String?,
    val accountType: AccountType?,
    val accountTypeNameFallback: String?,
    /** Grouped in fours, Persian digits, meant to be laid out left-to-right. */
    val accountNumber: String,
    val startDate: String?,
    val endDate: String?,
    val isActive: Boolean,
)
