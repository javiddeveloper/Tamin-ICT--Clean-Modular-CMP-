package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * The app's standard labelled text input.
 *
 * Places the [label] as a title above the input box (matching [PickerRow] and [SelectableField]),
 * and renders a styled rounded outlined text field with theme tokens.
 */
@Composable
fun TaminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val colors = LocalTaminColors.current
    val showError = isError || errorMessage != null

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            isError = showError,
            shape = RoundedCornerShape(CornerRadius.lg),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = textStyle.copy(fontWeight = FontWeight.SemiBold),
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = textStyle.copy(fontWeight = FontWeight.Normal),
                        color = colors.textMuted,
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.blueText,
                unfocusedBorderColor = colors.border,
                errorBorderColor = colors.dangerText,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                disabledTextColor = colors.textPrimary,
                disabledBorderColor = colors.border,
                disabledContainerColor = colors.bgSurface,
                cursorColor = colors.blueText,
                focusedContainerColor = colors.bgSurface,
                unfocusedContainerColor = colors.bgSurface,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        if (errorMessage != null) {
            Text(
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
            TaminTextField(value = "", onValueChange = {}, label = "کد کارگاه", placeholder = "مثلاً ۱۲۳۴۵۶")
            TaminTextField(value = "۹۹۰۰۰۲۰", onValueChange = {}, label = "کد کارگاه")
            TaminTextField(
                value = "۱۲",
                onValueChange = {},
                label = "کد کارگاه",
                errorMessage = "کد کارگاه معتبر نیست",
            )
            TaminTextField(
                value = "۰۹۱۴۳۰۱۸۳۷۲",
                onValueChange = {},
                label = "تلفن همراه",
                enabled = false,
            )
        }
    }
}
