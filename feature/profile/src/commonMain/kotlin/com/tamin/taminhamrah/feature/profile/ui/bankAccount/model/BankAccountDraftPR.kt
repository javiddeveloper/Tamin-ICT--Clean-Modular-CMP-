package com.tamin.taminhamrah.feature.profile.ui.bankAccount.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.bankAccount.AccountType
import com.tamin.taminhamrah.model.bankAccount.Bank
import com.tamin.taminhamrah.ui.digitsOnly

/**
 * What the user has filled in so far, and whether it may be sent.
 *
 * Pure so the rules can be tested without a ViewModel: every decision the form makes is a function
 * of these five fields.
 */
@Immutable
data class BankAccountDraftPR(
    val startDateMillis: Long? = null,
    /** Jalali, already formatted for display — the picker produces it, the form only shows it. */
    val startDateLabel: String? = null,
    val bank: Bank? = null,
    val accountType: AccountType? = null,
    val accountNumber: String = "",
) {

    /**
     * The digits actually sent.
     *
     * The help sheet tells the user to read "این ۱۸ رقم" off their card, but that is the account
     * segment of a 26-character IBAN, which is zero-padded to 18 — a رفاه account is 9 digits.
     * Someone who follows the instruction literally types the padding too, so leading zeros are
     * dropped when, and only when, they are what makes the number too long. A number that is short,
     * or long for any other reason, is left alone and reported as wrong.
     */
    val normalizedAccountNumber: String
        get() {
            val digits = accountNumber.digitsOnly()
            val expected = bank?.accountNumberLength ?: return digits
            // Stops at the expected length rather than eating every leading zero: a zero that is
            // part of the account number itself has to survive.
            var trimmed = digits
            while (trimmed.length > expected && trimmed.startsWith('0')) {
                trimmed = trimmed.substring(1)
            }
            return trimmed
        }

    /**
     * The first thing wrong with the form, or null when it may be sent.
     *
     * The bank is judged before the number on purpose: the bank is what decides how many digits the
     * number needs, so "wrong length" cannot be stated until one is chosen.
     */
    fun firstError(): BankAccountFormError? = when {
        startDateMillis == null || bank == null ||
            accountType == null || accountNumber.digitsOnly().isEmpty() ->
            BankAccountFormError.Incomplete

        normalizedAccountNumber.length != bank.accountNumberLength ->
            BankAccountFormError.WrongLength(bank.accountNumberLength)

        else -> null
    }

    val canSubmit: Boolean get() = firstError() == null
}

/**
 * Carried rather than resolved: a ViewModel that calls `getString` hangs the unit tests, so the
 * screen turns this into the message the user reads.
 */
sealed interface BankAccountFormError {
    data object Incomplete : BankAccountFormError
    data class WrongLength(val expected: Int) : BankAccountFormError
}
