package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * The app's single-line text input.
 *
 * `SegmentedInputField` covers fixed-length codes typed into slots — a national ID, an OTP — and
 * `PickerRow` covers values chosen rather than typed. Anything freely typed and of unknown length
 * had no shared component, so screens reached for Material's `OutlinedTextField` directly and each
 * dressed it differently. This is that field, wearing the theme's own tokens.
 *
 * Reports a problem the same way the rest of the form does: the border turns [LocalTaminColors]'
 * danger colour and [errorMessage], when given, sits under the field.
 */
@Composable
fun TaminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val colors = LocalTaminColors.current
    val showError = isError || errorMessage != null

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            isError = showError,
            shape = RoundedCornerShape(CornerRadius.lg),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            label = label?.let { { TaminText(text = it, color = colors.textSecondary) } },
            placeholder = placeholder?.let { { TaminText(text = it, color = colors.textMuted) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.blueText,
                unfocusedBorderColor = colors.border,
                errorBorderColor = colors.dangerText,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                disabledTextColor = colors.textMuted,
                cursorColor = colors.blueText,
                focusedContainerColor = colors.bgSurface,
                unfocusedContainerColor = colors.bgSurface,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        if (errorMessage != null) {
            TaminText(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = colors.dangerText,
                modifier = Modifier.padding(horizontal = Spacing.sm),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTextFieldPreview() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            TaminTextField(value = "", onValueChange = {}, label = "کد کارگاه")
            TaminTextField(value = "۹۹۰۰۰۲۰", onValueChange = {}, label = "کد کارگاه")
            TaminTextField(
                value = "۱۲",
                onValueChange = {},
                label = "کد کارگاه",
                errorMessage = "کد کارگاه معتبر نیست",
            )
            TaminTextField(
                value = "",
                onValueChange = {},
                label = "غیر فعال",
                enabled = false,
                placeholder = "قابل ویرایش نیست",
            )
        }
    }
}
