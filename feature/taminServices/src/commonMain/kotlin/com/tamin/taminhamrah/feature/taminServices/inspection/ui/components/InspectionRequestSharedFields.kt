package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits

/**
 * The mobile/landline/email row shared verbatim by [com.tamin.taminhamrah.feature.taminServices.inspection.ui.steps.IdentityContactStep]
 * and the employer-side `WorkshopIdentityContactStep` it was ported from. Validity and the mobile
 * field's label/required-ness are passed in rather than computed here, since the two flows validate
 * mobile differently (workshop enforces the real `09`+9-digit pattern; the insured flow still only
 * checks length).
 */
@Composable
internal fun ContactDetailsFields(
    mobile: String,
    onMobileChange: (String) -> Unit,
    isMobileValid: Boolean?,
    mobileLabel: String,
    mobileErrorText: String,
    landline: String,
    onLandlineChange: (String) -> Unit,
    isLandlineValid: Boolean?,
    landlineLabel: String,
    landlineErrorText: String,
    email: String,
    onEmailChange: (String) -> Unit,
    isEmailValid: Boolean?,
    emailLabel: String,
    emailErrorText: String,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TaminStyledTextField(
            value = mobile,
            onValueChange = onMobileChange,
            label = mobileLabel,
            placeholder = placeholder,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            inputRestriction = InputRestriction.DigitsOnly,
            maxLength = 11,
            isValid = isMobileValid,
            errorText = mobileErrorText,
        )

        Spacer(Modifier.height(Spacing.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TaminStyledTextField(
                value = landline,
                onValueChange = onLandlineChange,
                label = landlineLabel,
                placeholder = placeholder,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                inputRestriction = InputRestriction.DigitsOnly,
                maxLength = 11,
                isValid = isLandlineValid,
                errorText = landlineErrorText,
                modifier = Modifier.weight(1f),
            )
            TaminStyledTextField(
                value = email,
                onValueChange = onEmailChange,
                label = emailLabel,
                placeholder = placeholder,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isValid = isEmailValid,
                errorText = emailErrorText,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * The description textarea + live character counter shared verbatim by
 * [com.tamin.taminhamrah.feature.taminServices.inspection.ui.steps.RequestDescriptionStep] and the
 * employer-side `WorkshopRequestDescriptionStep`. The info banner underneath is intentionally left
 * to each call site — the two flows currently use two different banner components (`InfoBanner` vs
 * `BannerCard`), which is a separate, pre-existing visual inconsistency this dedup doesn't resolve.
 */
@Composable
internal fun RequestDescriptionTextField(
    description: String,
    onDescriptionChange: (String) -> Unit,
    label: String,
    placeholder: String,
    maxLength: Int,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
) {
    Column(modifier = modifier) {
        TaminTextArea(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = placeholder,
            label = label,
            isRequired = isRequired,
            maxLength = maxLength,
        )

        Spacer(Modifier.height(Spacing.xs))

        TaminText(
            text = "${
                description.length.toString().toPersianDigits()
            }/${maxLength.toString().toPersianDigits()}",
            style = MaterialTheme.typography.labelSmall,
            color = LocalTaminColors.current.textMuted,
        )
    }
}
