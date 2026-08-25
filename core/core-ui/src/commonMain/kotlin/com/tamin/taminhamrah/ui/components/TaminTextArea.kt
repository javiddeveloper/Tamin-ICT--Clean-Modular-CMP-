package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_info

/**
 * Multi-line counterpart to [SegmentedInputField]'s field chrome: a labeled, bordered box that
 * grows with its content instead of a single row. Shares the same error treatment -- the border
 * animates to [com.tamin.taminhamrah.ui.theme.SemanticColors.dangerText] via [animatedErrorBorder]
 * and a warning row appears underneath -- so a form mixing single-line fields and a text area
 * reports problems the same way throughout.
 */
@Composable
fun TaminTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isRequired: Boolean = false,
    enabled: Boolean = true,
    error: Boolean = false,
    errorMessage: String? = null,
    minLines: Int = 4,
    maxLines: Int = 6,
    maxLength: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val colors = LocalTaminColors.current
    var isFocused by remember { mutableStateOf(false) }

    val annotatedLabel = buildAnnotatedString {
        append(label)
        if (isRequired) {
            withStyle(SpanStyle(color = colors.dangerText)) {
                append(" *")
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        TaminText(
            text = annotatedLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textMuted,
            modifier = Modifier.padding(bottom = Spacing.xs),
        )

        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(maxLength?.let { newValue.take(it) } ?: newValue)
            },
            enabled = enabled,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.blueText),
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgSurface, RoundedCornerShape(CornerRadius.listRow))
                .animatedErrorBorder(
                    isError = error,
                    errorColor = colors.dangerText,
                    normalColor = if (isFocused) colors.blueText else colors.border,
                    borderWidth = Thickness.border,
                    cornerRadius = CornerRadius.listRow,
                )
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    TaminText(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textMuted,
                    )
                }
                innerTextField()
            },
        )

        if (error && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_info),
                    contentDescription = null,
                    tint = colors.dangerText,
                    modifier = Modifier.size(IconSize.small),
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                TaminText(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.dangerText,
                )
            }
        }
    }
}
