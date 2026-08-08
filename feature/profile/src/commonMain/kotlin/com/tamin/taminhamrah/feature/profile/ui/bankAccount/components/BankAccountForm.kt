package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountPicker
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountDraftPR
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountFormError
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.components.animatedErrorBorder
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_digit_counter
import taminx.core.core_ui.bank_account_digits_of_bank
import taminx.core.core_ui.bank_account_error_incomplete
import taminx.core.core_ui.bank_account_error_length
import taminx.core.core_ui.bank_account_field_bank
import taminx.core.core_ui.bank_account_field_number
import taminx.core.core_ui.bank_account_field_start_date
import taminx.core.core_ui.bank_account_field_type
import taminx.core.core_ui.bank_account_form_title
import taminx.core.core_ui.bank_account_help_mobile_bank
import taminx.core.core_ui.bank_account_help_ussd
import taminx.core.core_ui.bank_account_number_hint
import taminx.core.core_ui.bank_account_submit
import taminx.core.core_ui.ic_arrow_down

private const val PARAGRAPH_BREAK = "\n\n"

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
    isSubmitting: Boolean,
    onPickerRequested: (BankAccountPicker) -> Unit,
    onAccountNumberChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val bank = draft.bank
    val error = draft.firstError()
    // Nothing is scolded until a submit has been attempted.
    val incomplete = showValidation && error == BankAccountFormError.Incomplete
    val wrongLength = showValidation && error is BankAccountFormError.WrongLength

    val bankLabel = bank?.label?.let { stringResource(it) }
    val expected = bank?.accountNumberLength ?: WIDEST_ACCOUNT
    val entered = draft.normalizedAccountNumber.length

    val numberError = when {
        // Names the bank, what it wants and what was typed -- "13 digits" alone leaves the reader
        // counting their own input to find the discrepancy.
        wrongLength && bankLabel != null -> stringResource(
            Res.string.bank_account_error_length,
            bankLabel,
            expected.toString().toPersianDigits(),
            entered.toString().toPersianDigits(),
        )

        incomplete && draft.accountNumber.isEmpty() ->
            stringResource(Res.string.bank_account_error_incomplete)

        else -> null
    }

    // Before a bank is chosen the hint explains why the length is unknown; after, it states it.
    val numberHint = if (bankLabel == null) {
        stringResource(Res.string.bank_account_number_hint)
    } else {
        stringResource(
            Res.string.bank_account_digits_of_bank,
            bankLabel,
            expected.toString().toPersianDigits(),
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeaderTitle(title = stringResource(Res.string.bank_account_form_title))
        Spacer(Modifier.height(Spacing.sm))

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

        AccountNumberRow(
            value = draft.accountNumber,
            onValueChange = onAccountNumberChanged,
            counter = stringResource(
                Res.string.bank_account_digit_counter,
                entered.toString().toPersianDigits(),
                expected.toString().toPersianDigits(),
            ),
            isError = numberError != null,
        )
        Spacer(Modifier.height(Spacing.sm))

        // One blue info block, as the design draws it: a single icon over the length rule and the
        // two ways to look the number up. Joined once per change rather than on every keystroke.
        val helpMobileBank = stringResource(Res.string.bank_account_help_mobile_bank)
        val helpUssd = stringResource(Res.string.bank_account_help_ussd)
        val helpMessage = remember(numberHint, helpMobileBank, helpUssd) {
            listOf(numberHint, helpMobileBank, helpUssd).joinToString(PARAGRAPH_BREAK)
        }
        BannerCard(message = helpMessage, type = BannerType.Info)

        if (numberError != null) {
            Spacer(Modifier.height(Spacing.sm))
            BannerCard(message = numberError, type = BannerType.Error)
        }

        Spacer(Modifier.height(Spacing.lg))
        // Carries its own spinner and disabled tone, so the screen needs no overlay while the
        // request is in flight.
        LoadingButton(
            text = stringResource(Res.string.bank_account_submit),
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
            isLoading = isSubmitting,
        )
    }
}

/**
 * The account number: one bordered row with the digits on the reading edge and the live count
 * opposite them, both inside the box the design draws.
 *
 * A plain field rather than [SegmentedInputField]: the final design shows no slots, and the count
 * is what tells the reader how many digits are still owed.
 */
@Composable
private fun AccountNumberRow(
    value: String,
    onValueChange: (String) -> Unit,
    counter: String,
    isError: Boolean,
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
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        BasicTextField(
            value = value,
            onValueChange = { raw -> onValueChange(raw.digitsOnly()) },
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            cursorBrush = SolidColor(colors.blueText),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.bank_account_field_number),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textMuted,
                    )
                }
                inner()
            },
        )
        Text(
            text = counter,
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) colors.dangerText else colors.textMuted,
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
        // Label first so it takes the reading edge and the affordance sits opposite it, which is
        // the left in a right-to-left layout -- where the design puts every one of these icons.
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPlaceholder) colors.textMuted else colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = icon ?: vectorResource(Res.drawable.ic_arrow_down),
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(19.dp),
        )
    }
}
