package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.rememberTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.calculate_wage_pension_amount_label
import taminx.core.core_ui.calculate_wage_pension_cd_info
import taminx.core.core_ui.calculate_wage_pension_chip_estimated
import taminx.core.core_ui.calculate_wage_pension_chip_legal_floor
import taminx.core.core_ui.calculate_wage_pension_chip_simultaneous
import taminx.core.core_ui.calculate_wage_pension_title
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.unit_rial

/**
 * Hero header built on [TaminTopAppBar]: circular back/info in the bar row, large title +
 * amount + status chips in the content slot. The expanded-only block (subtitle, amount, chips)
 * folds away entirely as [topAreaState] collapses, handing off to a title that fades into the bar
 * row instead -- same split as `ActiveRelationHeader`. [bottomPadding] is the caller's reserved
 * bottom space for the overlapping stats card to ride into (see `CalculateWagePensionScreen`'s
 * `CalculateWagePensionTopArea`), same pattern as profile + `ValidationStatusCard`.
 */
@Composable
internal fun CalculateWagePensionHeader(
    eligibleAmount: Long,
    legalFloorApplied: Boolean,
    isMultipleWorkshopsEnabled: Boolean,
    onBack: () -> Unit,
    onInfoClick: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = Spacing.lg,
    isLoading: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val gradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    TaminTopAppBar(
        title = stringResource(Res.string.calculate_wage_pension_title),
        modifier = modifier,
        background = gradient,
        bottomPadding = bottomPadding,
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = stringResource(Res.string.back_content_description),
                onClick = onBack,
                bordered = true,
            )
        },
        action = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_info),
                contentDescription = stringResource(Res.string.calculate_wage_pension_cd_info),
                onClick = onInfoClick,
                bordered = true,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaHide(topAreaState)
                .padding(horizontal = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
     /*       Text(
                text = stringResource(Res.string.calculate_wage_pension_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.onGradient,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Spacing.md))*/

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = stringResource(Res.string.calculate_wage_pension_amount_label),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textHeaderSubtitle,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            if (isLoading) {
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.helperLineWidth)
                        .height(ShimmerSize.wageValueHeight),
                    cornerRadius = CornerRadius.md,
                    colorBase = colors.onGradient.copy(alpha = 0.2f),
                    colorHighlight = colors.onGradient.copy(alpha = 0.4f),
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = eligibleAmount.toPriceFormat().toPersianDigits(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.onGradient,
                    )
                    Text(
                        text = stringResource(Res.string.unit_rial),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onGradient.copy(alpha = 0.85f),
                        modifier = Modifier.padding(start = Spacing.sm, bottom = Spacing.xs),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            if (isLoading) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    ShimmerBlock(
                        modifier = Modifier
                            .width(ShimmerSize.chipWidth)
                            .height(ShimmerSize.badgeHeight),
                        cornerRadius = CornerRadius.chip,
                        colorBase = colors.onGradient.copy(alpha = 0.2f),
                        colorHighlight = colors.onGradient.copy(alpha = 0.4f),
                    )
                    ShimmerBlock(
                        modifier = Modifier
                            .width(ShimmerSize.hintWidth)
                            .height(ShimmerSize.badgeHeight),
                        cornerRadius = CornerRadius.chip,
                        colorBase = colors.onGradient.copy(alpha = 0.2f),
                        colorHighlight = colors.onGradient.copy(alpha = 0.4f),
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    val secondaryLabel = when {
                        isMultipleWorkshopsEnabled ->
                            stringResource(Res.string.calculate_wage_pension_chip_simultaneous)
                        legalFloorApplied ->
                            stringResource(Res.string.calculate_wage_pension_chip_legal_floor)
                        else -> null
                    }
                    if (secondaryLabel != null) {
                        StatusPill(
                            text = secondaryLabel,
                            containerColor = colors.onGradient.copy(alpha = 0.12f),
                            contentColor = colors.onGradient,
                            borderColor = colors.onGradient.copy(alpha = 0.35f),
                        )
                    }
                    StatusPill(
                        text = stringResource(Res.string.calculate_wage_pension_chip_estimated),
                        containerColor = colors.onGradient.copy(alpha = 0.12f),
                        contentColor = colors.onGradient,
                        borderColor = colors.onGradient.copy(alpha = 0.35f),
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xs))
        }
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionHeaderPreview() {
    PreviewRtlThemeContent {
        CalculateWagePensionHeader(
            eligibleAmount = 12_450_000L,
            legalFloorApplied = true,
            isMultipleWorkshopsEnabled = false,
            onBack = {},
            onInfoClick = {},
            topAreaState = rememberTopAreaState(224.dp, 64.dp),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionHeaderLoadingPreview() {
    PreviewRtlThemeContent {
        CalculateWagePensionHeader(
            eligibleAmount = 0L,
            legalFloorApplied = false,
            isMultipleWorkshopsEnabled = false,
            onBack = {},
            onInfoClick = {},
            isLoading = true,
            topAreaState = rememberTopAreaState(224.dp, 64.dp),
        )
    }
}
