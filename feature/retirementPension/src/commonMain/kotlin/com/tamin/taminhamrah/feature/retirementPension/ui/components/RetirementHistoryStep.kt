package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.pension.retirement.RetirementHistoryPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_alert_triangle
import taminx.core.core_ui.retirement_pension_amount_rial
import taminx.core.core_ui.retirement_pension_average_wage
import taminx.core.core_ui.retirement_pension_estimated_pension
import taminx.core.core_ui.retirement_pension_history_card_title
import taminx.core.core_ui.retirement_pension_history_note
import taminx.core.core_ui.retirement_pension_history_objection
import taminx.core.core_ui.retirement_pension_history_total_days_label
import taminx.core.core_ui.retirement_pension_history_total_days_value
import taminx.core.core_ui.retirement_pension_history_unit_day
import taminx.core.core_ui.retirement_pension_history_unit_month
import taminx.core.core_ui.retirement_pension_history_unit_year

private val ObjectionRowHeight = 48.dp
private val TileShimmerHeight = 58.dp
private val CellShimmerHeight = 52.dp

/**
 * Step 5 — what the service has on record and what it is worth, with a way to object.
 *
 * The whole step is read-only, so nothing here validates: the objection is a different service.
 */
@Composable
internal fun RetirementHistoryStep(
    history: RetirementHistoryPR?,
    isLoading: Boolean,
    onRegisterObjection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RetirementStepColumn(modifier = modifier) {
        AnimatedContent(
            targetState = isLoading || history == null,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "RetirementHistoryBody",
        ) { loading ->
            if (loading) HistoryShimmer() else HistoryBody(history!!)
        }

        BannerCard(
            message = stringResource(Res.string.retirement_pension_history_note),
            type = BannerType.Info,
            // The design reads its informational prose in slate; only the glyph is blue.
            textColor = LocalTaminColors.current.textSecondary,
        )

        ObjectionRow(onClick = onRegisterObjection)
    }
}

@Composable
private fun HistoryBody(history: RetirementHistoryPR) {
    val colors = LocalTaminColors.current

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        ) {
            StatTile(
                label = stringResource(Res.string.retirement_pension_average_wage),
                amount = stringResource(
                    Res.string.retirement_pension_amount_rial,
                    history.averageWage,
                ),
                containerColor = colors.bgSurface,
                contentColor = colors.textPrimary,
                labelColor = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(Res.string.retirement_pension_estimated_pension),
                amount = stringResource(
                    Res.string.retirement_pension_amount_rial,
                    history.estimatedPension,
                ),
                containerColor = colors.blueText,
                contentColor = colors.bgSurface,
                labelColor = colors.bgSurface,
                modifier = Modifier.weight(1f),
            )
        }

        RetirementCard {
            TaminText(
                text = stringResource(Res.string.retirement_pension_history_card_title),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = Spacing.smPlus),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                DurationCell(history.years, stringResource(Res.string.retirement_pension_history_unit_year), Modifier.weight(1f))
                DurationCell(history.months, stringResource(Res.string.retirement_pension_history_unit_month), Modifier.weight(1f))
                DurationCell(history.days, stringResource(Res.string.retirement_pension_history_unit_day), Modifier.weight(1f))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.smPlus)
                    .background(colors.blueBg, RoundedCornerShape(CornerRadius.listRow))
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = stringResource(Res.string.retirement_pension_history_total_days_label),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textSecondary,
                )
                TaminText(
                    text = stringResource(
                        Res.string.retirement_pension_history_total_days_value,
                        history.totalDays,
                    ),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.blueText,
                )
            }
        }
    }
}

@Composable
private fun DurationCell(value: String, unit: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.listRow))
            .padding(vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        NumericText(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        TaminText(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
    }
}

@Composable
private fun HistoryShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        ) {
            repeat(2) {
                ShimmerBlock(
                    modifier = Modifier
                        .weight(1f)
                        .height(TileShimmerHeight),
                    cornerRadius = CornerRadius.xl,
                )
            }
        }
        RetirementCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                repeat(3) {
                    ShimmerBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(CellShimmerHeight),
                        cornerRadius = CornerRadius.listRow,
                    )
                }
            }
        }
    }
}

@Composable
private fun ObjectionRow(onClick: () -> Unit) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.chip)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ObjectionRowHeight)
            .clip(shape)
            .background(colors.dangerBg)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_alert_triangle),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.small),
        )
        TaminText(
            text = stringResource(Res.string.retirement_pension_history_objection),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.dangerText,
        )
    }
}
