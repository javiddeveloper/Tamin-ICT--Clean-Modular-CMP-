package com.tamin.taminhamrah.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import com.tamin.taminhamrah.ui.components.TaminDivider
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * The chooser behind a filter chip.
 *
 * Material's own menu rather than a panel of our own: it anchors to the chip that opened it,
 * animates out of that anchor, and brings the platform's outside-tap and back handling with it.
 */
@Composable
fun <T> RecordFilterMenu(
    expanded: Boolean,
    options: ImmutableList<Pair<T, String>>,
    isSelected: (T) -> Boolean,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
) {
    val colors = LocalTaminColors.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        // The menu keeps the card surface the panel had: menu defaults are a tighter radius and
        // a tonal fill, which read as a system menu dropped onto the screen rather than as ours.
        shape = RoundedCornerShape(CornerRadius.card),
        containerColor = colors.bgSurface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = Elevation.md,
    ) {
        options.forEach { (value, label) ->
            val selected = isSelected(value)
            DropdownMenuItem(
                modifier = if (selected) Modifier.background(colors.greenBg) else Modifier,
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selected) colors.teal else colors.textPrimary,
                    )
                },
                trailingIcon = if (!selected) {
                    null
                } else {
                    {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_tamin_check),
                            contentDescription = null,
                            tint = colors.teal,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                },
                onClick = { onSelect(value) },
            )
        }
    }
}

@Immutable
data class ActionMenuItem<T>(
    val value: T,
    val label: String,
    val icon: DrawableResource,
    val isDestructive: Boolean = false,
    val hasDivider: Boolean = false,
    val isWarningIcon: Boolean = false,
)

@Composable
fun <T> RecordActionMenu(
    expanded: Boolean,
    items: ImmutableList<ActionMenuItem<T>>,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
) {
    val colors = LocalTaminColors.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CornerRadius.card),
        containerColor = colors.bgSurface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = Elevation.md,
    ) {
        items.forEachIndexed { index, item ->
            val textColor = if (item.isDestructive) colors.dangerText else colors.textPrimary
            val iconColor = when {
                item.isDestructive -> colors.dangerText
                item.isWarningIcon -> colors.warning
                else -> colors.textPrimary
            }
            DropdownMenuItem(
                text = { Text(item.label, color = textColor, style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(item.icon),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(IconSize.small),
                    )
                },
                onClick = { onSelect(item.value) },
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
            )
            if (item.hasDivider && index < items.lastIndex) {
                TaminDivider(modifier = Modifier.padding(horizontal = Spacing.md))
            }
        }
    }
}
