package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

/**
 * The app's standard labelled text input.
 *
 * `SegmentedInputField` covers fixed-length codes typed into slots — a national ID, an OTP — and
 * `PickerRow` covers values chosen rather than typed. Anything freely typed and of unknown length
 * had no shared component, so screens reached for Material's `OutlinedTextField` directly and each
 * dressed it differently. This is that field, wearing the theme's own tokens.
 *
 * Reports a problem the same way the rest of the form does: the border turns [LocalTaminColors]'
 * danger color and [errorMessage], when given, sits under the field. Set [animateErrorBorder] to
 * draw that outline with [animatedErrorBorder] instead, so a rejected value sweeps to the danger
 * color like the segmented fields and text areas beside it.
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
    maxLength: Int? = null,
    /**
     * When [value] already has [maxLength] characters, only deletions are accepted. Insertions and
     * in-place replacements are ignored until the user shortens the field, then typing can continue
     * up to [maxLength] again.
     */
    deleteOnlyWhenFull: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    /**
     * Swaps Material's outline for [animatedErrorBorder].
     *
     * Off by default: the animated stroke replaces the focus outline too, and the screens already
     * using this field were built against Material's look.
     */
    animateErrorBorder: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val showError = isError || errorMessage != null
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    // Keep the field in sync when the caller filters/clamps to the same logical value — otherwise
    // Compose leaves the over-length keystroke visible because `value` did not change.
    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length),
            )
        }
    }

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

        val shape = RoundedCornerShape(CornerRadius.lg)
        // Material's outline is switched off only when the animated one replaces it: two strokes on
        // the same rounded rect read as one doubled hairline.
        val outline = Color.Transparent.takeIf { animateErrorBorder }
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { incoming ->
                val proposed = incoming.text
                val next = when {
                    maxLength == null -> proposed
                    deleteOnlyWhenFull && value.length >= maxLength && proposed.length >= value.length ->
                        value
                    else -> proposed.take(maxLength)
                }
                textFieldValue = when {
                    next == value && proposed != value ->
                        TextFieldValue(text = value, selection = TextRange(value.length))
                    next.length < proposed.length ->
                        TextFieldValue(text = next, selection = TextRange(next.length))
                    else ->
                        incoming.copy(
                            text = next,
                            selection = TextRange(
                                incoming.selection.start.coerceIn(0, next.length),
                                incoming.selection.end.coerceIn(0, next.length),
                            ),
                        )
                }
                if (next != value) {
                    onValueChange(next)
                }
            },
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            isError = showError,
            shape = shape,
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
                focusedBorderColor = outline ?: colors.blueText,
                unfocusedBorderColor = outline ?: colors.border,
                errorBorderColor = outline ?: colors.dangerText,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                disabledTextColor = colors.textPrimary,
                disabledBorderColor = colors.border,
                disabledContainerColor = colors.bgSurface,
                cursorColor = colors.blueText,
                focusedContainerColor = colors.bgSurface,
                unfocusedContainerColor = colors.bgSurface,
            ),
            modifier = if (animateErrorBorder) {
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(colors.bgSurface)
                    .animatedErrorBorder(
                        isError = showError,
                        errorColor = colors.dangerText,
                        normalColor = colors.border,
                        borderWidth = Thickness.border,
                        cornerRadius = CornerRadius.lg,
                    )
            } else {
                Modifier.fillMaxWidth()
            },
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
