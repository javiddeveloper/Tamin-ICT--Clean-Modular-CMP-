package com.tamin.taminhamrah.model.bankAccount

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_type_current
import taminx.core.core_ui.bank_account_type_current_companion
import taminx.core.core_ui.bank_account_type_interest_free
import taminx.core.core_ui.bank_account_type_savings
import taminx.core.core_ui.bank_account_type_savings_companion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * The account kinds the register call accepts. Hard-coded because the previous app hard-coded them
 * too — there is no endpoint that serves this list.
 *
 * The list endpoint returns the same set as objects carrying `accountCode` and `accountName`; the
 * code is what matches, the name is only the server's own spelling of it.
 */
enum class AccountType(val code: String, val label: StringResource) {
    INTEREST_FREE("01", Res.string.bank_account_type_interest_free),
    SAVINGS("02", Res.string.bank_account_type_savings),
    SAVINGS_COMPANION("03", Res.string.bank_account_type_savings_companion),
    CURRENT("04", Res.string.bank_account_type_current),
    CURRENT_COMPANION("05", Res.string.bank_account_type_current_companion),
    ;

    companion object {
        /** The order the picker lists them in, as the design does. */
        val displayOrder: ImmutableList<AccountType> = persistentListOf(
            SAVINGS_COMPANION, SAVINGS, CURRENT_COMPANION, CURRENT, INTEREST_FREE,
        )

        fun fromCode(code: String?): AccountType? =
            code?.trim()?.let { wanted -> entries.firstOrNull { it.code == wanted } }
    }
}
