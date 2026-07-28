package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Components for the contracted-centres screen: the search field that lives in the teal
 * header, and the result row for one hospital, pharmacy, lab or clinic.
 */

/**
 * One contracted center: a type-colored icon badge, the center's name and category,
 * its address, and the distance beside a tap-to-call action.
 */
@Composable
fun MedicalCenterCard(
    name: String,
    type: String,
    address: String,
    distanceLabel: String,
    icon: ImageVector,
    accentColor: Color,
    accentContainerColor: Color,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
    distanceIcon: ImageVector? = null,
    callIcon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(TreatmentDimens.centerBadgeSize)
                .background(
                    accentContainerColor,
                    RoundedCornerShape(CornerRadius.listRow),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(IconSize.medium),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            CenterNameRow(
                name = name,
                type = type,
                accentColor = accentColor,
                accentContainerColor = accentContainerColor,
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall,
                color = LocalTaminColors.current.textSecondary,
            )
            CenterActionRow(
                distanceLabel = distanceLabel,
                distanceIcon = distanceIcon,
                callIcon = callIcon,
                onCallClick = onCallClick,
            )
        }
    }
}

@Composable
private fun CenterNameRow(
    name: String,
    type: String,
    accentColor: Color,
    accentContainerColor: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = LocalTaminColors.current.textPrimary,
        )
        StatusPill(
            text = type,
            containerColor = accentContainerColor,
            contentColor = accentColor,
        )
    }
}

@Composable
private fun CenterActionRow(
    distanceLabel: String,
    distanceIcon: ImageVector?,
    callIcon: ImageVector?,
    onCallClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.padding(top = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (distanceIcon != null) {
                Icon(
                    imageVector = distanceIcon,
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(IconSize.small),
                )
            }
            Text(
                text = distanceLabel,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textTertiary,
            )
        }
        Row(
            modifier = Modifier.clickable(onClick = onCallClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (callIcon != null) {
                Icon(
                    imageVector = callIcon,
                    contentDescription = null,
                    tint = colors.teal,
                    modifier = Modifier.size(IconSize.small),
                )
            }
            Text(
                text = "تماس",
                style = MaterialTheme.typography.labelMedium,
                color = colors.teal,
            )
        }
    }
}
