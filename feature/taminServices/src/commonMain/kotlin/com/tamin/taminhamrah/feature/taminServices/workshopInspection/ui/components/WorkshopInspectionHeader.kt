package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.rememberTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.inspection_count_format
import taminx.core.core_ui.workshop_inspection_banner_subtitle
import taminx.core.core_ui.workshop_inspection_search_action_content_description
import taminx.core.core_ui.workshop_inspection_subtitle
import taminx.core.core_ui.workshop_inspection_title

/**
 * Header for the employer workshop-inspections list. Unlike the insured-side
 * `InspectionHeader` (a centered ring-icon + subtitle), the design here embeds a glass
 * summary card directly inside the collapsing gradient panel — icon tile, title/subtitle
 * and inspection count laid out the same way as `LegalRepresentativeWorkshopSummaryCard`
 * (`feature/workshops`). See `docs/vault/Workshop-Inspection-Status.md`.
 */
@Composable
internal fun WorkshopInspectionHeader(
    count: Int,
    onBackClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier,
    topAreaState: TopAreaState,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(gradient),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.workshop_inspection_title),
            background = gradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                    bordered = true
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = stringResource(Res.string.workshop_inspection_search_action_content_description),
                    onClick = onSearchClicked,
                    bordered = true
                )
            },
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .topAreaHide(topAreaState)
                    .clip(RoundedCornerShape(CornerRadius.card))
                    .background(taminColors.glassIconTileBg)
                    .border(1.dp, taminColors.glassIconTileBorder, RoundedCornerShape(CornerRadius.card))
                    .padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                GlassIconTile(
                    modifier = Modifier.size(IconSize.xlarge),
                    icon = Icons.Outlined.Assignment,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    Text(
                        text = stringResource(Res.string.workshop_inspection_subtitle),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.onGradient,
                    )
                    Text(
                        text = stringResource(Res.string.workshop_inspection_banner_subtitle),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                        color = taminColors.onGradient.copy(alpha = 0.85f),
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    NumericText(
                        text = count.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.onGradient,
                    )
                    Text(
                        text = stringResource(Res.string.inspection_count_format),
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.onGradient.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopInspectionHeaderPreviewLight() {
    PreviewRtlThemeContent {
        WorkshopInspectionHeader(
            count = 4,
            onBackClicked = {},
            onSearchClicked = {},
            topAreaState = rememberTopAreaState(224.dp, 64.dp)
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopInspectionHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        WorkshopInspectionHeader(
            count = 4,
            onBackClicked = {},
            onSearchClicked = {},
            topAreaState = rememberTopAreaState(224.dp, 64.dp)
        )
    }
}
