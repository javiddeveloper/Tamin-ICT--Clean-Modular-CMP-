package com.tamin.taminhamrah.mapper.bankAccount

import com.tamin.taminhamrah.model.bankAccount.AccountType
import com.tamin.taminhamrah.model.bankAccount.Bank
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountPR
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.groupedFromEnd
import com.tamin.taminhamrah.util.toPersianDigits
import kotlin.jvm.JvmName

@JvmName("bankAccountListToPresentation")
fun List<BankAccountDN>.toPresentation(): List<BankAccountPR> = map { it.toPresentation() }

fun BankAccountDN.toPresentation(): BankAccountPR {
    val bank = Bank.fromCode(this.bank?.bankCode)
    val type = AccountType.fromCode(accounType?.accountCode)
    return BankAccountPR(
        id = id ?: 0L,
        bank = bank,
        bankNameFallback = this.bank?.bankName?.takeIf { bank == null },
        accountType = type,
        accountTypeNameFallback = accounType?.accountName?.takeIf { type == null },
        // Grouped as the design prints it, then converted, so the arithmetic stays on ASCII digits.
        accountNumber = accountNumber.orEmpty().digitsOnly().groupedFromEnd().toPersianDigits(),
        startDate = PersianDateFormatter.formatTimestamp(dateOfStart).takeIf { it.isNotEmpty() },
        endDate = PersianDateFormatter.formatTimestamp(dateOfFinish).takeIf { it.isNotEmpty() },
        // `isValidAccount` comes back null even for accounts the service lists as confirmed,
        // so `confirmed` is the only field that actually says anything here.
        isActive = confirmed == true,
    )
}
