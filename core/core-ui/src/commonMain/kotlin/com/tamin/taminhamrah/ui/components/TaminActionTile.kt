package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * A card that offers one action: an icon tile, what it does, and a line saying what to expect.
 *
 * The shape pages use to put two or three choices at the foot of a screen — download this, send
 * that — side by side. The tile's colours are the caller's, because they are what tells the actions
 * apart at a glance.
 */
@Composable
fun TaminActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    iconBackground: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(Hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = PaddingH, vertical = PaddingV),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(TileSize)
                .clip(RoundedCornerShape(TileCorner))
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(IconSize),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = Spacing.xs),
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
    }
}

private val Hairline = 1.dp
private val PaddingH = 13.dp
private val PaddingV = 12.dp
private val TileSize = 34.dp
private val TileCorner = 12.dp
private val IconSize = 18.dp
