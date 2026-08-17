package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_all_detail_title
import taminx.feature.history.history_all_no_workshop
import taminx.feature.history.history_all_season_autumn
import taminx.feature.history.history_all_season_spring
import taminx.feature.history.history_all_season_summer
import taminx.feature.history.history_all_season_winter
import taminx.feature.history.history_all_workshops
import taminx.feature.history.history_all_year_days

/** Months to a season, and the four the Jalali year is read in. */
private const val MONTHS_PER_SEASON = 3

private val SeasonLabels: List<StringResource> = listOf(
    Res.string.history_all_season_spring,
    Res.string.history_all_season_summer,
    Res.string.history_all_season_autumn,
    Res.string.history_all_season_winter,
)

/**
 * One year's months, and the workshops that reported it.
 *
 * Reads season by season rather than as twelve rows, which is how the previous app grouped it and
 * how a year is actually read. [workshops] is what the wage endpoint returned for this year — empty
 * when it returned nothing, which the sheet says out loud instead of showing a blank panel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearDetailSheet(
    year: YearHistoryPR,
    workshops: ImmutableList<DastmozdInfoItemPR>,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            SheetHeader(year = year)

            for (season in SeasonLabels.indices) {
                SeasonBlock(
                    label = stringResource(SeasonLabels[season]),
                    firstMonth = season * MONTHS_PER_SEASON,
                    monthDays = year.monthDays,
                )
            }

            Text(
                text = stringResource(Res.string.history_all_workshops),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )

            if (workshops.isEmpty()) {
                Text(
                    text = stringResource(Res.string.history_all_no_workshop),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            } else {
                workshops.forEach { workshop ->
                    WorkshopRow(workshop = workshop)
                }
            }
        }
    }
}

@Composable
private fun SheetHeader(year: YearHistoryPR) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.history_all_detail_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        NumericText(
            text = year.year.toPersianDigits(),
            style = MaterialTheme.typography.titleLarge,
            color = colors.blueText,
        )
        Text(
            text = stringResource(
                Res.string.history_all_year_days,
                year.totalDays.toString().toPersianDigits(),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

/**
 * Three months of one season.
 *
 * Indexed rather than chunked: `chunked` would allocate four lists every time the sheet redraws,
 * and the offsets are fixed.
 */
@Composable
private fun SeasonBlock(
    label: String,
    firstMonth: Int,
    monthDays: ImmutableList<Int>,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgPage)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            for (offset in 0 until MONTHS_PER_SEASON) {
                val month = firstMonth + offset
                MonthCell(
                    name = PersianDateFormatter.monthNames.getOrElse(month) { "" },
                    days = monthDays.getOrElse(month) { 0 },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MonthCell(
    name: String,
    days: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.chip))
            .background(colors.bgSurface)
            .padding(vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
        )
        NumericText(
            text = days.toString().toPersianDigits(),
            style = MaterialTheme.typography.titleSmall,
            color = if (days > 0) colors.textPrimary else colors.textMuted,
        )
    }
}

@Composable
private fun WorkshopRow(workshop: DastmozdInfoItemPR) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgPage)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = workshop.rwshname,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = workshop.brhname,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            Box(modifier = Modifier.weight(1f))
            Text(
                text = workshop.historytypedesc,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }
    }
}

/** Nothing recorded for this year, which is a legitimate answer rather than a failure. */
internal val NoWorkshops: ImmutableList<DastmozdInfoItemPR> = persistentListOf()
