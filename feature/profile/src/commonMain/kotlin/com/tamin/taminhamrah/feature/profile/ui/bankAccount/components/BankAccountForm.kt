package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountPicker
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountDraftPR
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountFormError
import com.tamin.taminhamrah.model.bankAccount.Bank
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.animatedErrorBorder
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_error_incomplete
import taminx.core.core_ui.bank_account_error_length
import taminx.core.core_ui.bank_account_field_bank
import taminx.core.core_ui.bank_account_field_number
import taminx.core.core_ui.bank_account_field_start_date
import taminx.core.core_ui.bank_account_field_type
import taminx.core.core_ui.bank_account_form_title
import taminx.core.core_ui.bank_account_number_hint
import taminx.core.core_ui.bank_account_submit
import taminx.core.core_ui.bank_account_where_on_card
import taminx.core.core_ui.ic_arrow_down

/** The widest account any bank takes, used before one is chosen so the field has a shape. */
private const val WIDEST_ACCOUNT = 13

/**
 * The register form.
 *
 * Field errors are the app's existing treatment: [SegmentedInputField] already draws the animated
 * danger border and the icon-and-text line beneath, and the three picker rows borrow the same
 * [animatedErrorBorder] so a missing bank looks exactly like a bad number.
 */
@Composable
fun BankAccountForm(
    draft: BankAccountDraftPR,
    showValidation: Boolean,
    onPickerRequested: (BankAccountPicker) -> Unit,
    onAccountNumberChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val error = draft.firstError()
    // Nothing is scolded until a submit has been attempted.
    val incomplete = showValidation && error == BankAccountFormError.Incomplete
    val wrongLength = showValidation && error is BankAccountFormError.WrongLength

    val numberError = when {
        wrongLength -> stringResource(
            Res.string.bank_account_error_length,
            (error as BankAccountFormError.WrongLength).expected.toString().toPersianDigits(),
        )

        incomplete && draft.accountNumber.isEmpty() ->
            stringResource(Res.string.bank_account_error_incomplete)

        else -> null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.bank_account_form_title),
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.md))

        PickerRow(
            text = draft.startDateLabel ?: stringResource(Res.string.bank_account_field_start_date),
            isPlaceholder = draft.startDateLabel == null,
            isError = incomplete && draft.startDateMillis == null,
            onClick = { onPickerRequested(BankAccountPicker.DATE) },
        )
        Spacer(Modifier.height(Spacing.sm))

        PickerRow(
            text = draft.bank?.label?.let { stringResource(it) }
                ?: stringResource(Res.string.bank_account_field_bank),
            isPlaceholder = draft.bank == null,
            isError = incomplete && draft.bank == null,
            onClick = { onPickerRequested(BankAccountPicker.BANK) },
        )
        Spacer(Modifier.height(Spacing.sm))

        PickerRow(
            text = draft.accountType?.label?.let { stringResource(it) }
                ?: stringResource(Res.string.bank_account_field_type),
            isPlaceholder = draft.accountType == null,
            isError = incomplete && draft.accountType == null,
            onClick = { onPickerRequested(BankAccountPicker.TYPE) },
        )
        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = stringResource(Res.string.bank_account_field_number),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(Spacing.xs))

        // The slot count is the chosen bank's digit count, so the field itself states the length
        // the help sheet only describes.
        SegmentedInputField(
            value = draft.accountNumber,
            onValueChange = onAccountNumberChanged,
            slotCount = draft.bank?.accountNumberLength ?: WIDEST_ACCOUNT,
            error = numberError != null,
            errorMessage = numberError,
        )
        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = stringResource(Res.string.bank_account_number_hint),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = stringResource(Res.string.bank_account_where_on_card),
            style = MaterialTheme.typography.labelSmall,
            color = colors.blueText,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { onPickerRequested(BankAccountPicker.IBAN_HELP) },
        )

        Spacer(Modifier.height(Spacing.lg))
        TaminPrimaryButton(
            text = stringResource(Res.string.bank_account_submit),
            onClick = onSubmit,
        )
    }
}

/**
 * A row that opens a picker. Not a text field, but it reports a problem like one — same animated
 * border, same danger color — so the form reads as a single control set.
 */
@Composable
private fun PickerRow(
    text: String,
    isPlaceholder: Boolean,
    isError: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .animatedErrorBorder(
                isError = isError,
                errorColor = colors.dangerText,
                normalColor = colors.textMuted,
                borderWidth = Thickness.border,
                cornerRadius = CornerRadius.lg,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = icon ?: vectorResource(Res.drawable.ic_arrow_down),
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(19.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPlaceholder) colors.textMuted else colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}
