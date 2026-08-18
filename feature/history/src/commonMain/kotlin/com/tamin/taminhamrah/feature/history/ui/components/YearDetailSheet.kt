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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_combined_detail_title
import taminx.feature.history.history_combined_no_workshop
import taminx.feature.history.history_combined_season_autumn
import taminx.feature.history.history_combined_season_spring
import taminx.feature.history.history_combined_season_summer
import taminx.feature.history.history_combined_season_winter
import taminx.feature.history.history_combined_workshops
import taminx.feature.history.history_combined_workshops_unavailable
import taminx.feature.history.history_combined_wage_from_1386
import taminx.feature.history.history_combined_month_esfand_leap
import taminx.feature.history.history_combined_year_days

/** Months to a season, and the four the Jalali year is read in. */
private const val MONTHS_PER_SEASON = 3

/** Index of اسفند, the only month whose length depends on the year. */
private const val ESFAND_INDEX = 11

/**
 * The first year `dastmozdinfos` holds anything for.
 *
 * The service is «سوابق و ریز دستمزد بعد از سال ۸۶» — earlier years have no wage rows at all, so
 * their empty panel is a fact about the service rather than about this person's employers.
 */
private const val FIRST_WAGE_YEAR = 1386

private val SeasonLabels: List<StringResource> = listOf(
    Res.string.history_combined_season_spring,
    Res.string.history_combined_season_summer,
    Res.string.history_combined_season_autumn,
    Res.string.history_combined_season_winter,
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
    wagesUnavailable: Boolean,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Read once per year rather than per redraw: it decides one label and never changes under it.
    val jalaliYear = remember(year.year) { year.year.toIntOrNull() }
    val isLeapYear = remember(jalaliYear) {
        jalaliYear?.let { PersianDateFormatter.isLeapYear(it) } == true
    }

    /*
     * Closing runs the hide animation to its end and only then reports the dismissal.
     *
     * The sheet lives in the tree because the state holds a selected year, so telling the state
     * first would take the sheet out from under its own animation and it would disappear on the
     * spot. Every way out — the scrim, the drag, the system back — goes through here, so they all
     * slide out the same way.
     */
    val dismiss: () -> Unit = {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion { if (!sheetState.isVisible) onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState,
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
                    isLeapYear = isLeapYear,
                )
            }

            Text(
                text = stringResource(Res.string.history_combined_workshops),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )

            if (workshops.isEmpty()) {
                // Three different blanks, and only one of them means "no workshops": the wage call
                // failing is not knowing, and a year before the service began is not knowable.
                Text(
                    text = stringResource(
                        when {
                            wagesUnavailable ->
                                Res.string.history_combined_workshops_unavailable

                            jalaliYear != null && jalaliYear < FIRST_WAGE_YEAR ->
                                Res.string.history_combined_wage_from_1386

                            else -> Res.string.history_combined_no_workshop
                        },
                    ),
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
            text = stringResource(Res.string.history_combined_detail_title),
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
                Res.string.history_combined_year_days,
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
    isLeapYear: Boolean,
) {
    val colors = LocalTaminColors.current
    // The previous app marked اسفند in a leap year the same way, and it is the one month whose
    // length a reader cannot infer from the calendar in their head.
    val esfandLabel = stringResource(Res.string.history_combined_month_esfand_leap)

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
                    name = if (month == ESFAND_INDEX && isLeapYear) {
                        esfandLabel
                    } else {
                        PersianDateFormatter.monthNames.getOrElse(month) { "" }
                    },
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

/**
 * One workshop's year: who it was, and what it paid month by month.
 *
 * The wage rows come off the wire as `hismonN`/`hiswageN` pairs, and the model keeps the wire's
 * names: `WageDetailPR.month` carries the **days** worked, and the month itself is the position in
 * the list. Read that way here rather than renamed, so the field the service sends and the field
 * the app reads stay the same field.
 */
@Composable
private fun WorkshopRow(workshop: DastmozdInfoItemPR) {
    val colors = LocalTaminColors.current

    // Only the months this workshop actually reported. A year at one employer is a handful of
    // months, and twelve rows of zero would bury them. Kept in a remember so scrolling the sheet
    // does not re-filter twelve entries per workshop per frame.
    val activeMonths = remember(workshop) {
        workshop.wageDetails.mapIndexedNotNull { index, detail ->
            val days = detail.month.toIntOrNull() ?: 0
            val wage = detail.wage.toLongOrNull() ?: 0L
            if (days == 0 && wage == 0L) null else WorkedMonth(index, days, detail.wage)
        }
    }

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

        activeMonths.forEach { worked ->
            WorkedMonthRow(worked = worked)
        }
    }
}

/** A month this workshop reported: which one, how many days, and what it paid. */
private data class WorkedMonth(
    val monthIndex: Int,
    val days: Int,
    val wage: String,
)

@Composable
private fun WorkedMonthRow(worked: WorkedMonth) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = PersianDateFormatter.monthNames.getOrElse(worked.monthIndex) { "" },
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
        Text(
            text = stringResource(
                Res.string.history_combined_year_days,
                worked.days.toString().toPersianDigits(),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Box(modifier = Modifier.weight(1f))
        NumericText(
            text = worked.wage.toRialAmount().toPersianDigits(),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
        )
    }
}

/** Nothing recorded for this year, which is a legitimate answer rather than a failure. */
internal val NoWorkshops: ImmutableList<DastmozdInfoItemPR> = persistentListOf()
