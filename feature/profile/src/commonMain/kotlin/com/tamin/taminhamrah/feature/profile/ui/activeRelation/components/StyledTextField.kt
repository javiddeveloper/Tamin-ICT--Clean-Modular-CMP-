package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.applicationFont

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    leadingIconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    trailingIcon: ImageVector? = null,
    trailingIconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    isValid: Boolean? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    isRequired: Boolean = false,
    onClick: (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    val taminColors = LocalTaminColors.current
    var isFocused by remember { mutableStateOf(false) }

    // Colors mapping based on focus and validation state
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
                    .background(taminColors.bgSurface, RoundedCornerShape(13.dp))
                    .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(13.dp))
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        onFocusChanged?.invoke(focusState.isFocused)
                    }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon
                if (leadingIconPainter != null) {
                    Icon(
                        painter = leadingIconPainter,
                        contentDescription = null,
                        tint = leadingIconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                } else if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = leadingIconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Input field
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    readOnly = readOnly || onClick != null,
                    keyboardOptions = keyboardOptions,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = taminColors.textPrimary,
                        fontWeight = FontWeight.Medium,
                        fontFamily = applicationFont()
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            TaminText(
                                text = placeholder,
                                fontSize = 13.5.sp,
                                color = taminColors.textMuted,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                )

                // Suffix validation / status checkmark or trailing icon
                if (isValid == true) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "معتبر",
                        tint = taminColors.greenText,
                        modifier = Modifier.size(19.dp)
                    )
                } else if (trailingIconPainter != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = trailingIconPainter,
                        contentDescription = null,
                        tint = trailingIconColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = trailingIconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Error message below
            if (isValid == false && !errorText.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "خطا",
                        tint = taminColors.dangerText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TaminText(
                        text = errorText,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.dangerText
                    )
                }
            }
        }

        // Overlay transparent Box over entire field when onClick != null
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(13.dp))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() }
            )
        }
    }
}
