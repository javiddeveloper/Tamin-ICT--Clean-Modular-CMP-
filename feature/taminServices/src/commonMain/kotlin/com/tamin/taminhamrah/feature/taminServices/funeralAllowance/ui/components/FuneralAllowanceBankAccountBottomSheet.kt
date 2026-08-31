package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.bankAccount.BankAccountPR
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuneralAllowanceBankAccountBottomSheet(
    bankAccounts: List<BankAccountPR>,
    selectedAccount: BankAccountPR?,
    onSelect: (BankAccountPR) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LocalTaminColors.current.bgPage
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.lg)
        ) {
            TaminText(
                text = "حساب بانکی جهت واریز",
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
            TaminText(
                text = "یکی از حساب‌های ثبت‌شدهٔ خود را انتخاب کنید",
                color = LocalTaminColors.current.textSecondary,
                modifier = Modifier.padding(bottom = Spacing.lg)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(bankAccounts) { account ->
                    val isSelected = account.id == selectedAccount?.id
                    BankAccountItem(
                        account = account,
                        isSelected = isSelected,
                        onClick = {
                            onSelect(account)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BankAccountItem(
    account: BankAccountPR,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val bankName = account.bank?.label?.let { org.jetbrains.compose.resources.stringResource(it) } ?: account.bankNameFallback ?: ""
    val accountType = account.accountType?.label?.let { org.jetbrains.compose.resources.stringResource(it) } ?: account.accountTypeNameFallback ?: ""
    val combinedName = if (accountType.isNotBlank()) "$bankName - $accountType" else bankName

    val bgColor = if (isSelected) taminColors.blueBg else taminColors.bgSurface
    val borderColor = if (isSelected) taminColors.blueText else taminColors.border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = taminColors.blueText,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(Spacing.md))

        Column(modifier = Modifier.weight(1f)) {
            TaminText(
                text = combinedName,
                color = taminColors.textPrimary
            )
            TaminText(
                text = account.accountNumber,
                color = taminColors.textMuted
            )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun BankAccountBottomSheetPreview() {
    PreviewRtlThemeContent {
        FuneralAllowanceBankAccountBottomSheet(
            bankAccounts = listOf(
                com.tamin.taminhamrah.model.bankAccount.BankAccountPR(
                    id = 1,
                    bank = null,
                    bankNameFallback = "بانک ملت",
                    accountType = null,
                    accountTypeNameFallback = "کوتاه‌مدت",
                    accountNumber = "1234567890",
                    startDate = null,
                    endDate = null,
                    isActive = true
                ),
                com.tamin.taminhamrah.model.bankAccount.BankAccountPR(
                    id = 2,
                    bank = null,
                    bankNameFallback = "بانک ملی",
                    accountType = null,
                    accountTypeNameFallback = "قرض‌الحسنه",
                    accountNumber = "0987654321",
                    startDate = null,
                    endDate = null,
                    isActive = false
                )
            ),
            selectedAccount = com.tamin.taminhamrah.model.bankAccount.BankAccountPR(
                id = 1,
                bank = null,
                bankNameFallback = "بانک ملت",
                accountType = null,
                accountTypeNameFallback = "کوتاه‌مدت",
                accountNumber = "1234567890",
                startDate = null,
                endDate = null,
                isActive = true
            ),
            onSelect = {},
            onDismiss = {}
        )
    }
}
