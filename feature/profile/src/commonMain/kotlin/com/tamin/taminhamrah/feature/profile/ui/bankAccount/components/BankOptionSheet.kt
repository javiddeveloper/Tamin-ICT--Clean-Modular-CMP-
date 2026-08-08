package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.bankAccount.AccountType
import com.tamin.taminhamrah.model.bankAccount.Bank
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_digits_suffix

private val LogoSize = 28.dp

/**
 * The bank chooser. Each row carries its logo, which is the fastest way to find a bank in a list
 * of six and the reason the marks were cropped away from their wordmarks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankPickerSheet(
    title: String,
    onSelect: (Bank) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.page, vertical = Spacing.md),
        ) {
            SheetTitle(title)
            Bank.entries.forEach { bank ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(bank) }
                        .padding(vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Image(
                        painter = painterResource(bank.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(LogoSize),
                    )
                    Text(
                        text = stringResource(bank.label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    // The row states the bank's digit count so the choice is made knowing it.
                    Text(
                        text = stringResource(
                            Res.string.bank_account_digits_suffix,
                            bank.accountNumberLength.toString().toPersianDigits(),
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            }
        }
    }
}

/** The account-kind chooser: five fixed options, no logos. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTypePickerSheet(
    title: String,
    onSelect: (AccountType) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.page, vertical = Spacing.md),
        ) {
            SheetTitle(title)
            AccountType.entries.forEach { type ->
                Text(
                    text = stringResource(type.label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(type) }
                        .padding(vertical = Spacing.md),
                )
            }
        }
    }
}

@Composable
private fun SheetTitle(title: String) {
    val colors = LocalTaminColors.current
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = colors.textPrimary,
    )
    Spacer(Modifier.height(Spacing.sm))
}
