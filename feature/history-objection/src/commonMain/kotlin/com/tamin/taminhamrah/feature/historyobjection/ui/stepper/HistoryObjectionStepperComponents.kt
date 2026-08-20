package com.tamin.taminhamrah.feature.historyobjection.ui.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

private val FieldHeight = 56.dp

@Composable
internal fun HistoryObjectionStepTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = LocalTaminColors.current.textPrimary,
        modifier = modifier,
    )
}

@Composable
internal fun HistoryObjectionFieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = LocalTaminColors.current.textMuted,
        modifier = modifier,
    )
}

/** A clickable, label-above field row that opens a picker — the 4 dropdown fields in step 1. */
@Composable
internal fun HistoryObjectionSelectableFieldRow(
    label: String,
    value: String,
    placeholder: String,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        HistoryObjectionFieldLabel(label)
        Spacer(modifier = Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldHeight)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.lg))
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value.ifBlank { placeholder },
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.isBlank()) colors.textMuted else colors.textPrimary,
            )
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = colors.textMuted,
            )
        }
    }
}

/** A label-above, single-line free-text field — step 2's workshop name/employer/address fields. */
@Composable
internal fun HistoryObjectionTextFieldRow(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val colors = LocalTaminColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        HistoryObjectionFieldLabel(label)
        Spacer(modifier = Modifier.height(Spacing.sm))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldHeight)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.lg))
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.blueText),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted,
                        )
                    }
                    innerTextField()
                },
            )
        }
    }
}
