package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.ui.components.AutoResizeText
import com.tamin.taminhamrah.ui.components.IconTile
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.startToEndGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminTeal500
import com.tamin.taminhamrah.ui.theme.TaminTeal900

/**
 * Components for the treatment hub landing screen: the quick-access and navigation cards, the
 * service category grid and the yearly cost summary. The insured-person card, its morph and its
 * carousel live in InsuranceCardComponents.kt.
 *
 * None of these depend on a domain model — they take display strings and lambdas, so
 * they stay decoupled from whatever shape the treatment entities settle on.
 */

/** The prominent teal gradient behind the "سوابق درمانی من" quick-access card. */
@Composable
fun quickAccessGradient(): Brush = startToEndGradient(listOf(TaminTeal900, TaminTeal500))
/**
 * One square tile in the hub's three-up service grid — electronic prescriptions,
 * medical confirmations, miscellaneous claims.
 */
@Composable
fun CategoryTile(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .raisedCard(CornerRadius.cardCompact)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm, vertical = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        IconTile(
            icon = icon,
            tint = iconTint,
            background = iconBackground,
            size = TreatmentDimens.categoryTileIconSize,
            cornerRadius = CornerRadius.lg,
        )
        AutoResizeText(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxs),
        )
    }
}

/**
 * Yearly spend summary: the insured person's share beside the organization's share.
 */
@Composable
fun CostSummaryCard(
    title: String,
    insuredShareLabel: String,
    /** `null` while the year's totals are still loading; the tiles shimmer until they land. */
    insuredShareAmount: String?,
    organizationShareLabel: String,
    organizationShareAmount: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.card)
            .padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = insuredShareLabel,
                amount = insuredShareAmount,
                containerColor = colors.greenBg,
                contentColor = colors.greenText,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = organizationShareLabel,
                amount = organizationShareAmount,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
