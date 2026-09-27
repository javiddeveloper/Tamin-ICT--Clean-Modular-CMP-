package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

private val EmptyStateTileSize = 76.dp
private val EmptyStateTileCorner = 24.dp
private val EmptyStateTileShadowBlur = 20.dp
private val EmptyStateTileShadowOffsetY = 8.dp

@Composable
fun EmptyStateMessage(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    /** Sets the icon on a raised surface tile, as the newer empty states draw it. */
    showIconTile: Boolean = false,
    modifier: Modifier = Modifier,
    /**
     * Draws the action as a compact button on this gradient instead of a plain Material button. Null, the default, keeps the plain button every existing empty state has.
     */
    actionBackground: Brush? = null,
    /** An icon at the start of the gradient action — so on the right, on the RTL page. */
    actionIcon: ImageVector? = null,
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            val iconContent: @Composable () -> Unit = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.large),
                    tint = if (showIconTile) taminColors.chevron else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (showIconTile) {
                Box(
                    modifier = Modifier
                        .size(EmptyStateTileSize)
                        // Before the clip, so it draws outside the tile. Without it the tile is
                        // white on a near-white page and reads as no tile at all.
                        .coloredShadow(
                            color = taminColors.shadowSubtle,
                            borderRadius = EmptyStateTileCorner,
                            blurRadius = EmptyStateTileShadowBlur,
                            offsetY = EmptyStateTileShadowOffsetY,
                        )
                        .clip(RoundedCornerShape(EmptyStateTileCorner))
                        .background(taminColors.bgSurface)
                        .border(
                            width = Thickness.border,
                            color = taminColors.border,
                            shape = RoundedCornerShape(EmptyStateTileCorner),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    iconContent()
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
            } else {
                iconContent()
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                if (actionBackground != null) {
                    // Sized to its label rather than the page — [TaminPrimaryButton] always fills
                    // the width, which is too heavy under an empty state's few lines.
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(CornerRadius.xl))
                            .background(actionBackground)
                            .clickable(onClick = onAction)
                            .padding(horizontal = Spacing.lg, vertical = Spacing.smd),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (actionIcon != null) {
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = null,
                                tint = taminColors.onGradient,
                                modifier = Modifier.size(IconSize.small),
                            )
                        }
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = taminColors.onGradient,
                        )
                    }
                } else {
                    Button(onClick = onAction) {
                        Text(actionLabel)
                    }
                }
            }
        }
    }
}
