package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits

/**
 * An action inside a reply — a markdown link button, a deep link, a web link — on the app's
 * primary button gradient (`buttonGradient`, what the payment button is drawn with), so it
 * reads as a real call to action on the dark [AgentBackground] rather than the light theme's
 * pale blue chip, which showed up as a white block there.
 *
 * [compact] is the table-cell size: it fills its cell and wraps a long label onto a second
 * line, with no arrow. A disabled button (a target the app cannot act on) keeps the look at
 * the theme's disabled alpha.
 */
@Composable
internal fun AgentActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false,
    trailingIcon: ImageVector? = if (compact) null else Icons.AutoMirrored.Rounded.KeyboardArrowRight,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(if (compact) CornerRadius.md else CornerRadius.chip)
    Row(
        modifier = modifier
            .then(if (compact) Modifier.fillMaxWidth() else Modifier)
            .alpha(if (enabled) 1f else colors.disabledAlpha)
            .heightIn(min = if (compact) COMPACT_MIN_HEIGHT else MIN_HEIGHT)
            .clip(shape)
            .background(colors.buttonGradient)
            .border(Thickness.border, SheenBorder, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(
                horizontal = if (compact) Spacing.sm else Spacing.md,
                vertical = if (compact) Spacing.xs else Spacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
    ) {
        Text(
            text = label.toPersianDigits(),
            style = if (compact) {
                MaterialTheme.typography.labelSmall.copy(lineHeight = COMPACT_LINE_HEIGHT)
            } else {
                MaterialTheme.typography.labelLarge
            },
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = if (compact) Modifier.weight(1f, fill = false) else Modifier,
        )
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
            )
        }
    }
}

/** The same faint highlight edge the user's gradient bubble has. */
private val SheenBorder: Brush = Brush.linearGradient(
    listOf(Color.White.copy(alpha = 0.05f), Color.White.copy(alpha = 0.3f))
)
private val MIN_HEIGHT = 40.dp
private val COMPACT_MIN_HEIGHT = 32.dp
private val COMPACT_LINE_HEIGHT = 16.sp
