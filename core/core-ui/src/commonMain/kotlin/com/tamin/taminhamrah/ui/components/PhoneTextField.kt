package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TextDimens
import com.tamin.taminhamrah.ui.theme.Thickness
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

@Composable
fun PhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "۰۹— — — — — — — — —",
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.bgSurface,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .border(
                width = Thickness.border,
                color = if (isError) colors.dangerBorder else colors.blueText,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .size(IconSize.textFieldIconContainer)
                    .background(
                        color = colors.blueBg,
                        shape = RoundedCornerShape(CornerRadius.textFieldIcon)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.small),
                )
            }
        }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = colors.textPrimary,
                    textAlign = TextAlign.Right,
                    textDirection = TextDirection.Ltr,
                    letterSpacing = TextDimens.phoneLetterSpacing
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.textMuted,
                                textAlign = TextAlign.Right
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PhoneTextFieldLightPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        var phone by remember { mutableStateOf("") }
        PhoneTextField(
            value = phone,
            onValueChange = { phone = it },
            leadingIcon = Icons.Default.Phone,
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PhoneTextFieldDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        var phone by remember { mutableStateOf("09123456789") }
        PhoneTextField(
            value = phone,
            onValueChange = { phone = it },
            leadingIcon = Icons.Default.Phone,
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}
