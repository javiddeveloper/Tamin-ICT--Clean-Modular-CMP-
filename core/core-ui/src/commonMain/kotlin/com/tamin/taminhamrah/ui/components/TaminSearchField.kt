package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Search input styled to sit inside [TaminTopAppBar]: translucent over the bar's gradient
 * rather than filled with a surface color.
 *
 * The colors default to that translucent set, and are parameters rather than fixed so the same
 * field can also sit on a light sheet — same shape, same padding, same behavior, different ground.
 * A second copy of this for the light case would be the same twenty lines with four colors changed,
 * and would drift the first time one of them was adjusted.
 */
@Composable
fun TaminSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    searchIcon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.18f),
    contentColor: Color = Color.White,
    placeholderColor: Color = Color.White.copy(alpha = 0.7f),
    iconColor: Color = contentColor,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    /** A field that only ever takes a number of asks for the number pad. */
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val shape = RoundedCornerShape(CornerRadius.lg)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = searchIcon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(IconSize.small),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle.copy(color = contentColor),
            cursorBrush = SolidColor(contentColor),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = placeholderColor,
                    )
                }
                innerTextField()
            },
        )
    }
}
