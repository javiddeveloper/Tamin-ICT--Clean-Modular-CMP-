package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_iban_description
import taminx.core.core_ui.bank_account_iban_footer
import taminx.core.core_ui.bank_account_iban_label_account
import taminx.core.core_ui.bank_account_iban_label_bank
import taminx.core.core_ui.bank_account_iban_label_check
import taminx.core.core_ui.bank_account_iban_label_country
import taminx.core.core_ui.bank_account_iban_label_sheba
import taminx.core.core_ui.bank_account_iban_title
import taminx.core.core_ui.bank_account_iban_understood

// The worked example the design prints; the segments are what the labels point at.
private const val COUNTRY = "IR"
private const val CHECK = "31"
private const val BANK_CODE = "017"
private const val ACCOUNT = "000000023327878"

/**
 * Explains which slice of an IBAN the account number is.
 *
 * Worth its own sheet because the two numbers are easy to confuse: the account segment is
 * zero-padded inside the IBAN, which is why a رفاه account of nine digits appears as eighteen
 * there. The form accepts either — the padding is trimmed on the way out.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IbanExplainerSheet(onDismiss: () -> Unit) {
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
            Text(
                text = stringResource(Res.string.bank_account_iban_title),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(Res.string.bank_account_iban_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.md))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(TaminNavy900)
                    .padding(Spacing.md),
            ) {
                Text(
                    text = stringResource(Res.string.bank_account_iban_label_sheba),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.68f),
                )
                Spacer(Modifier.height(Spacing.xs))
                // An IBAN reads left to right even on an RTL page.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        IbanSegment(COUNTRY, Res.string.bank_account_iban_label_country, 2f)
                        IbanSegment(CHECK, Res.string.bank_account_iban_label_check, 2f)
                        IbanSegment(BANK_CODE, Res.string.bank_account_iban_label_bank, 3f)
                        IbanSegment(
                            value = ACCOUNT,
                            label = Res.string.bank_account_iban_label_account,
                            weight = 9f,
                            highlighted = true,
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.md))
            Text(
                text = stringResource(Res.string.bank_account_iban_footer),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.lg))
            TaminFilledButton(
                text = stringResource(Res.string.bank_account_iban_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.IbanSegment(
    value: String,
    label: org.jetbrains.compose.resources.StringResource,
    weight: Float,
    highlighted: Boolean = false,
) {
    Column(
        modifier = Modifier.weight(weight),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (highlighted) Color.White else Color.White.copy(alpha = 0.55f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = if (highlighted) 0.9f else 0.45f),
            textAlign = TextAlign.Center,
        )
    }
}
