package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * The card every سند اعتراض / پیامک‌ها screen shows right under its own toolbar: which objection
 * this is, and a shortcut to the sibling screen (SMS ↔ document).
 */
@Composable
fun ObjectionSummaryHeader(
    status: WorkShopObjectionStatus,
    objectionType: WorkShopObjectionType,
    objectionNumber: String,
    onNavigateToSibling: () -> Unit,
    siblingIcon: ImageVector,
    siblingContentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val (_, statusForeground) = status.tint.colors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.xlg)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(CornerRadius.xl))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {

        Box(
            modifier = Modifier
                .size(WorkshopSummaryIconSize)
                .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(CornerRadius.lg)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = siblingIcon,
                contentDescription = siblingContentDescription,
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(
                text = objectionType.label(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalAlignment = Alignment.CenterVertically) {
                NumericText(
                    text = objectionNumber,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f),
                )
                Text(
                    text = "شمارهٔ اعتراض",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.65f),
                )
            }
        }
        Row(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(CornerRadius.chip))
                .border(1.dp, Color.White.copy(alpha = 0.26f), RoundedCornerShape(CornerRadius.chip))
                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        ) {
            Text(
                text = status.label(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

private val WorkshopSummaryIconSize = 36.dp
