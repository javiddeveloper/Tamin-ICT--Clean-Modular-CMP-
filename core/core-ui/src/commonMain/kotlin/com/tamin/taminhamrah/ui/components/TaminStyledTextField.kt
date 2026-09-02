package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.applicationFont
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_content_description
import taminx.core.core_ui.valid_content_description

/**
 * Restricts what characters [TaminStyledTextField] accepts as the user types.
 */
enum class InputRestriction {
    None,
    LettersOnly,
    DigitsOnly,
    LettersAndDigits
}

/**
 * The app's stylized text input: bordered container, floating label, validation
 * tick/error state, and optional leading/trailing icon or read-only click overlay.
 *
 * Used across occurrence reporting, health self-declaration, and profile forms —
 * shared here instead of being copied per feature (see `.claude/rules/architecture.md`).
 */
@Composable
fun TaminStyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    leadingIconPainter: Painter? = null,
    trailingIcon: ImageVector? = null,
    trailingIconPainter: Painter? = null,
    isValid: Boolean? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    isRequired: Boolean = false,
    onClick: (() -> Unit)? = null,
    /** Whether the read-only [onClick] overlay ripples on press. */
    clickIndication: Boolean = true,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    maxLength: Int? = null,
    inputRestriction: InputRestriction = InputRestriction.None,
    textFieldBg: Color = LocalTaminColors.current.bgSurface
) {
    val taminColors = LocalTaminColors.current
    var isFocused by remember { mutableStateOf(false) }

    val borderColor = when {
        isValid == false -> taminColors.dangerText
        isFocused -> taminColors.blueText
        else -> taminColors.border
    }

    val leadingIconColor = if (isFocused) taminColors.blueText else taminColors.textMuted
    val trailingIconColor = if (isFocused) taminColors.blueText else taminColors.textSecondary

    val annotatedLabel = buildAnnotatedString {
        append(label)
        if (isRequired) {
            withStyle(SpanStyle(color = taminColors.dangerText)) {
                append(" *")
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (label.isNotBlank() || isRequired) {
                TaminText(
                    text = annotatedLabel,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textTertiary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(textFieldBg, RoundedCornerShape(13.dp))
                    .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(13.dp))
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        onFocusChanged?.invoke(focusState.isFocused)
                    }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIconPainter != null) {
                    Icon(painter = leadingIconPainter, contentDescription = null, tint = leadingIconColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                } else if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = leadingIconColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                }

                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        val restricted = when (inputRestriction) {
                            InputRestriction.None -> newValue
                            InputRestriction.LettersOnly -> newValue.filter { it.isLetter() || it.isWhitespace() }
                            InputRestriction.DigitsOnly -> newValue.filter { it.isDigit() }
                            InputRestriction.LettersAndDigits -> newValue.filter { it.isLetter() || it.isDigit() || it.isWhitespace() }
                        }
                        val limited = maxLength?.let { restricted.take(it) } ?: restricted
                        onValueChange(limited)
                    },
                    singleLine = singleLine,
                    readOnly = readOnly || onClick != null,
                    keyboardOptions = keyboardOptions,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = taminColors.textPrimary,
                        fontWeight = FontWeight.Medium,
                        fontFamily = applicationFont(),
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            TaminText(text = placeholder, fontSize = 13.5.sp, color = taminColors.textMuted, fontWeight = FontWeight.Normal)
                        }
                        innerTextField()
                    }
                )

                if (isValid == true) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = stringResource(Res.string.valid_content_description), tint = taminColors.greenText, modifier = Modifier.size(19.dp))
                } else if (trailingIconPainter != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(painter = trailingIconPainter, contentDescription = null, tint = trailingIconColor, modifier = Modifier.size(20.dp))
                } else if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = trailingIcon, contentDescription = null, tint = trailingIconColor, modifier = Modifier.size(20.dp))
                }
            }

            if (isValid == false && !errorText.isNullOrEmpty()) {
                Row(modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Error, contentDescription = stringResource(Res.string.error_content_description), tint = taminColors.dangerText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    TaminText(text = errorText, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = taminColors.dangerText)
                }
            }
        }

        if (onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(13.dp))
                    .clickable(
                        indication = if (clickIndication) ripple() else null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClick() }
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminStyledTextFieldPreview() {
    PreviewRtlThemeContent {
        var textValue by remember { mutableStateOf("09123456789") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .padding(16.dp)
        ) {
            TaminStyledTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = "شمارهٔ تلفن همراه",
                placeholder = "وارد کنید",
                leadingIcon = Icons.Default.Phone,
                isValid = textValue.length == 11,
                errorText = if (textValue.length != 11) "شماره همراه معتبر نیست" else null
            )
        }
    }
}
