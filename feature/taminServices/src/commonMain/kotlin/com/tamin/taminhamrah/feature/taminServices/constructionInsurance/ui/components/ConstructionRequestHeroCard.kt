package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.branch
import taminx.core.core_ui.file_number
import taminx.core.core_ui.ic_tamin_workshop
import taminx.core.core_ui.workshop_number

/**
 * The پرونده/کارگاه summary sitting on a top bar's own gradient, under the title — shared by every
 * construction-insurance detail screen (جزئیات درخواست, برگ پرداخت و گواهی). Same glass treatment
 * as `ObjectionSummaryHeader` (fixed white-alpha tones on the brand gradient, not theme-varying) —
 * icon tile on the leading (right, RTL) edge, details beside it.
 */
@Composable
fun ConstructionRequestHeroCard(
    fileNumber: Long?,
    workshopId: String?,
    branchCode: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(CornerRadius.xl))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(HeroIconSize)
                .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(CornerRadius.lg)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_workshop),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            HeroInfoLine(
                label = stringResource(Res.string.file_number),
                value = (fileNumber ?: 0).toString(),
            )
            HeroInfoLine(
                label = stringResource(Res.string.workshop_number),
                value = workshopId ?: "-",
                trailingLabel = branchCode?.takeIf { it.isNotBlank() }
                    ?.let { "${stringResource(Res.string.branch)} $it" },
            )
        }
    }
}

private val HeroIconSize = 36.dp

@Composable
private fun HeroInfoLine(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingLabel: String? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.65f),
        )
        NumericText(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
        if (trailingLabel != null) {
            Text(
                text = "·",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.5f),
            )
            Text(
                text = trailingLabel,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionRequestHeroCardPreview() {
    PreviewRtlThemeContent {
        ConstructionRequestHeroCard(
            fileNumber = 124037L,
            workshopId = "2361847",
            branchCode = "7",
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
