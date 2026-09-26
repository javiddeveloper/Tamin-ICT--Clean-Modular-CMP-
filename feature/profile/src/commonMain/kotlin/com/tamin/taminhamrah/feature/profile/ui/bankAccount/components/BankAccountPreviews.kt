package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountDraftPR
import com.tamin.taminhamrah.model.bankAccount.AccountType
import com.tamin.taminhamrah.model.bankAccount.Bank
import com.tamin.taminhamrah.model.bankAccount.BankAccountPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.groupedFromEnd
import com.tamin.taminhamrah.util.toPersianDigits

/**
 * The design's own sample rows, so a preview can be held against the mock directly.
 *
 * The number is formatted the way the mapper formats a real one rather than typed out already
 * grouped — a preview that hard-codes the spacing would still look right if the grouping broke.
 */
private fun sample(
    bank: Bank,
    accountType: AccountType,
    digits: String,
    startDate: String,
): BankAccountPR = BankAccountPR(
    id = bank.ordinal.toLong(),
    bank = bank,
    bankNameFallback = null,
    accountType = accountType,
    accountTypeNameFallback = null,
    accountNumber = digits.digitsOnly().groupedFromEnd().toPersianDigits(),
    startDate = startDate,
    endDate = null,
    isActive = true,
)

private val MelliAccount =
    sample(Bank.MELLI, AccountType.SAVINGS_COMPANION, "0102467483009", "۱۴۰۲/۰۴/۰۶")
private val MellatAccount =
    sample(Bank.MELLAT, AccountType.CURRENT_COMPANION, "4051627384", "۱۴۰۱/۱۱/۲۳")
private val RefahAccount =
    sample(Bank.REFAH, AccountType.INTEREST_FREE, "218745603", "۱۳۹۹/۰۶/۱۵")
private val TejaratAccount =
    sample(Bank.TEJARAT, AccountType.SAVINGS, "6540218937", "۱۴۰۳/۰۲/۰۹")
private val SepahAccount =
    sample(Bank.SEPAH, AccountType.CURRENT, "5801234567890", "۱۴۰۰/۰۹/۰۱")
private val SaderatAccount =
    sample(Bank.SADERAT, AccountType.SAVINGS_COMPANION, "0203145678902", "۱۴۰۴/۰۱/۱۹")

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardMelli() {
    PreviewRtlThemeContent { PreviewCard(MelliAccount) }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardMellat() {
    PreviewRtlThemeContent { PreviewCard(MellatAccount) }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardRefah() {
    PreviewRtlThemeContent { PreviewCard(RefahAccount) }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardTejarat() {
    PreviewRtlThemeContent { PreviewCard(TejaratAccount) }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardSepah() {
    PreviewRtlThemeContent { PreviewCard(SepahAccount) }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardSaderat() {
    PreviewRtlThemeContent { PreviewCard(SaderatAccount) }
}

/** All six together, which is the only way to judge whether the palettes sit as a set. */
@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardsAllBanks() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(PreviewPadding),
            verticalArrangement = Arrangement.spacedBy(PreviewPadding),
        ) {
            BankAccountCard(MelliAccount)
            BankAccountCard(MellatAccount)
            BankAccountCard(RefahAccount)
            BankAccountCard(TejaratAccount)
            BankAccountCard(SepahAccount)
            BankAccountCard(SaderatAccount)
        }
    }
}

/** A bank the service returns that the app has no palette for still has to draw. */
@PreviewRtlTheme
@Composable
private fun PreviewBankAccountCardUnknownBank() {
    PreviewRtlThemeContent {
        PreviewCard(
            BankAccountPR(
                id = 99L,
                bank = null,
                bankNameFallback = "بانک ناشناخته",
                accountType = null,
                accountTypeNameFallback = "پس انداز عادي",
                accountNumber = "۱۲۳۴ ۵۶۷ ۸۹۰",
                startDate = "۱۴۰۴/۰۵/۱۸",
                endDate = null,
                isActive = false,
            )
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountFormEmpty() {
    PreviewRtlThemeContent { PreviewForm(BankAccountDraftPR(), showValidation = false) }
}

/** رفاه, nine of nine: the state the design labels «تکمیل‌شده». */
@PreviewRtlTheme
@Composable
private fun PreviewBankAccountFormComplete() {
    PreviewRtlThemeContent {
        PreviewForm(
            draft = BankAccountDraftPR(
                startDateMillis = 0L,
                startDateLabel = "۱۴۰۴/۰۳/۱۲",
                bank = Bank.REFAH,
                accountType = AccountType.INTEREST_FREE,
                accountNumber = "218745603",
            ),
            showValidation = false,
        )
    }
}

/** ملی, nine of thirteen: the length error, which only shows after a submit attempt. */
@PreviewRtlTheme
@Composable
private fun PreviewBankAccountFormLengthError() {
    PreviewRtlThemeContent {
        PreviewForm(
            draft = BankAccountDraftPR(
                startDateMillis = 0L,
                startDateLabel = "۱۴۰۴/۰۲/۲۸",
                bank = Bank.MELLI,
                accountType = AccountType.SAVINGS_COMPANION,
                accountNumber = "010246748",
            ),
            showValidation = true,
        )
    }
}

/** Nothing filled in and submit pressed: every row reports at once. */
@PreviewRtlTheme
@Composable
private fun PreviewBankAccountFormIncomplete() {
    PreviewRtlThemeContent { PreviewForm(BankAccountDraftPR(), showValidation = true) }
}

/**
 * The two choosers.
 *
 * [BankPickerSheet] and [AccountTypePickerSheet] open a real [androidx.compose.material3.ModalBottomSheet],
 * which a preview renders as a full-screen scrim rather than in place, so these draw the sheets'
 * own content through [OptionSheetContent] -- the rows, dividers, tiles and chips are what
 * there is to check, and they are the part the design pins down.
 */
@PreviewRtlTheme
@Composable
private fun PreviewBankPickerSheetContent() {
    PreviewRtlThemeContent {
        OptionSheetContent(title = "انتخاب بانک") { BankPickerRows(onSelect = {}) }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewAccountTypePickerSheetContent() {
    PreviewRtlThemeContent {
        OptionSheetContent(title = "نوع حساب") { AccountTypePickerRows(onSelect = {}) }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountListSkeletonState() {
    PreviewRtlThemeContent { BankAccountListSkeleton() }
}

private val PreviewPadding = 14.dp

@Composable
private fun PreviewCard(account: BankAccountPR) {
    BankAccountCard(account = account, modifier = Modifier.padding(PreviewPadding))
}

@Composable
private fun PreviewForm(
    draft: BankAccountDraftPR,
    showValidation: Boolean,
) {
    BankAccountForm(
        draft = draft,
        showValidation = showValidation,
        onPickerRequested = {},
        onAccountNumberChanged = {},
        modifier = Modifier.fillMaxWidth().padding(PreviewPadding),
    )
}
