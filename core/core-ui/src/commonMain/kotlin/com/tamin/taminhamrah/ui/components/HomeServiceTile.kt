package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * The glassy rounded-square icon container used on the خدمات list and the home «دسترسی سریع» /
 * «خدمات ویژه» tiles. Same look as `feature:taminServices`'s `ServiceCard`, factored out so both
 * render identically.
 */
@Composable
fun ServiceIconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    dimmed: Boolean = false,
) {
    val isDark = LocalTaminColors.current == DarkTaminColors

    Box(
        modifier = modifier
            .size(IconSize.xlarge)
            .shadow(
                elevation = if (dimmed) Elevation.none else Elevation.md,
                shape = RoundedCornerShape(CornerRadius.iconTile),
                clip = false,
                ambientColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
                spotColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        if (isDark) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
                shape = RoundedCornerShape(CornerRadius.iconTile),
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.15f else 0.9f),
                        Color.White.copy(alpha = if (isDark) 0.02f else 0.1f),
                    ),
                ),
                shape = RoundedCornerShape(CornerRadius.iconTile),
            )
            .alpha(if (dimmed) 0.35f else 1f),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isDark) 0.05f else 0.6f),
                            Color.Transparent,
                        ),
                    ),
                    shape = RoundedCornerShape(CornerRadius.iconTile),
                ),
        )
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(IconSize.tileInner)
                .padding(Spacing.xxs),
        )
    }
}

/**
 * One cell of the home service grid: the icon tile with the service name beneath it. Disabled rows
 * are dimmed and not clickable; a small red dot marks any non-`ACTIVE` state, mirroring `ServiceCard`.
 */
@Composable
fun HomeServiceGridTile(
    service: MainServiceDN,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val disabled = service.status == MenuServiceStatusDN.DISABLED ||
        service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
        service.status == MenuServiceStatusDN.COMPLETELY_DISABLED
    val showDot = disabled || service.status == MenuServiceStatusDN.ENABLED_WITH_ERROR

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !disabled,
                onClick = onClick,
            )
            .padding(vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box {
            ServiceIconTile(
                icon = serviceIconFor(service.icon),
                contentDescription = service.name,
                dimmed = disabled,
            )
            if (showDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(IconSize.statIcon)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.75f), CircleShape),
                )
            }
        }
        Text(
            text = service.name.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(if (disabled) 0.5f else 1f),
        )
    }
}

/** A plain action tile ("همهٔ خدمات") that sits in the grid alongside the service tiles. */
@Composable
fun HomeActionGridTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        ServiceIconTile(icon = icon, contentDescription = label)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
