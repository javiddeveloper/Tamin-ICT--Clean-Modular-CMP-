package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.history.ui.model.WorkshopPR
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryPartialYearBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryPartialYearText
import com.tamin.taminhamrah.ui.theme.TaminHistorySeasonAutumn
import com.tamin.taminhamrah.ui.theme.TaminHistorySeasonSpring
import com.tamin.taminhamrah.ui.theme.TaminHistorySeasonSummer
import com.tamin.taminhamrah.ui.theme.TaminHistorySeasonWinter
import com.tamin.taminhamrah.ui.theme.TaminHistoryZeroText
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_close
import taminx.feature.history.history_combined_no_workshop
import taminx.feature.history.history_combined_season_autumn
import taminx.feature.history.history_combined_season_spring
import taminx.feature.history.history_combined_season_summer
import taminx.feature.history.history_combined_season_winter
import taminx.feature.history.history_combined_wage_unavailable
import taminx.feature.history.history_combined_year_days
import taminx.feature.history.history_copied
import taminx.feature.history.history_rial
import taminx.feature.history.history_sheet_title
import taminx.feature.history.history_workshop_branch
import taminx.feature.history.history_workshop_code
import taminx.feature.history.history_workshops_and_wages
import taminx.feature.history.history_year_full
import taminx.feature.history.history_year_incomplete
import com.tamin.taminhamrah.ui.components.plainTextClipEntry

/** Months to a season, and the four the Jalali year is read in. */
private const val MONTHS_PER_SEASON = 3

/**
 * One year in full: its twelve months by season, and every employer that reported it.
 *
 * Takes the folded [YearDetailPR] rather than raw rows — opening a sheet is then a render, not a
 * second pass over the year's wage data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearDetailSheet(
    detail: YearDetailPR,
    wagesUnavailable: Boolean,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    /*
     * Closing runs the hide animation to its end and only then reports the dismissal.
     *
     * The sheet lives in the tree because the state holds a selected year, so telling the state
     * first would take the sheet out from under its own animation, and it would disappear on the
     * spot. Every way out — the scrim, the drag, the system back — goes through here.
     */
    val dismiss: () -> Unit = {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion { if (!sheetState.isVisible) onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = SheetCorner, topEnd = SheetCorner),
    ) {
        /*
         * A LazyColumn, not a Column with verticalScroll.
         *
         * A plain scrolling container inside a bottom sheet does not take part in the sheet's
         * nested scrolling: dragging past the top of the content is handed to the sheet as an
         * expand gesture, which it cannot satisfy at full height, so it springs back — the sheet
         * visibly bouncing up and down. A lazy list cooperates with the sheet instead, and only
         * composes the workshop cards actually on screen.
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = SheetPaddingH,
                end = SheetPaddingH,
                bottom = SheetPaddingBottom,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            item(key = HEADER_KEY) { SheetHeader(detail = detail) }

            // Two cards per row — the design's grid, without nesting a grid inside a list.
            items(SEASONS / 2, key = { "season_row_$it" }) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    SeasonCard(detail = detail, season = row * 2, modifier = Modifier.weight(1f))
                    SeasonCard(detail = detail, season = row * 2 + 1, modifier = Modifier.weight(1f))
                }
            }

            item(key = WORKSHOPS_TITLE_KEY) {
                Text(
                    text = stringResource(Res.string.history_workshops_and_wages),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            }

            when {
                // Said plainly: the wage call failed, so "none recorded" would be a claim about
                // data that never arrived.
                wagesUnavailable -> item(key = WAGES_UNAVAILABLE_KEY) {
                    Text(
                        text = stringResource(Res.string.history_combined_wage_unavailable),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }

                detail.workshops.isEmpty() -> item(key = NO_WORKSHOP_KEY) {
                    Text(
                        text = stringResource(Res.string.history_combined_no_workshop),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }

                else -> items(detail.workshops, key = { it.id }) { WorkshopCard(workshop = it) }
            }

            item(key = CLOSE_KEY) { CloseButton(onClick = dismiss) }
        }
    }
}

private const val HEADER_KEY = "header"
private const val WORKSHOPS_TITLE_KEY = "workshops_title"
private const val WAGES_UNAVAILABLE_KEY = "wages_unavailable"
private const val NO_WORKSHOP_KEY = "no_workshop"
private const val CLOSE_KEY = "close"

@Composable
private fun SheetHeader(detail: YearDetailPR) {
    val colors = LocalTaminColors.current
    val complete = detail.totalDays >= FULL_YEAR_DAYS

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.history_sheet_title, detail.year.toPersianDigits()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = colors.textPrimary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            HeaderChip(
                text = stringResource(
                    if (complete) Res.string.history_year_full else Res.string.history_year_incomplete,
                ),
                textColor = if (complete) colors.blueText else TaminHistoryPartialYearText,
                background = if (complete) colors.blueBg else TaminHistoryPartialYearBg,
            )
            HeaderChip(
                text = stringResource(
                    Res.string.history_combined_year_days,
                    detail.totalDays.toString().toPersianDigits(),
                ),
                textColor = colors.textPrimary,
                background = colors.bgSurface,
                bordered = true,
            )
        }
    }
}

@Composable
private fun HeaderChip(
    text: String,
    textColor: Color,
    background: Color,
    bordered: Boolean = false,
) {
    val colors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(PillCorner))
            .background(background)
            .then(
                if (bordered) {
                    Modifier.border(Hairline, colors.border, RoundedCornerShape(PillCorner))
                } else {
                    Modifier
                },
            )
            .padding(horizontal = Spacing.sm, vertical = ChipPaddingV),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}

/** Three months under a colored dot — the season, as the design groups a year. */
@Composable
private fun SeasonCard(detail: YearDetailPR, season: Int, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val firstMonth = season * MONTHS_PER_SEASON

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(Hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = CardPaddingH, vertical = CardPaddingV),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(SeasonDot)
                    .clip(CircleShape)
                    .background(SeasonColors[season]),
            )
            Text(
                text = stringResource(SeasonLabels[season]),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
            )
        }

        for (offset in 0 until MONTHS_PER_SEASON) {
            val month = firstMonth + offset
            val days = detail.monthDays.getOrElse(month) { 0 }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = PersianDateFormatter.monthNames[month],
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                // A month with nothing is «۰۰», not «۰ روز» — the design keeps the column even.
                NumericText(
                    text = if (days > 0) {
                        days.toString().toPersianDigits()
                    } else {
                        EmptyMonth.toPersianDigits()
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = if (days > 0) colors.textPrimary else TaminHistoryZeroText,
                )
            }
        }
    }
}

/** One employer's year: who they were, where, and what they paid month by month. */
@Composable
private fun WorkshopCard(workshop: WorkshopPR) {
    val colors = LocalTaminColors.current
    val clipboard = LocalClipboard.current
    val copyScope = rememberCoroutineScope()
    var copied by remember(workshop.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(Hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
            .padding(CardPaddingH),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workshop.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.textPrimary,
                )
                Text(
                    text = workshop.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
            DaysPill(days = workshop.totalDays)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            FactTile(
                label = stringResource(Res.string.history_workshop_branch),
                value = workshop.branch,
                modifier = Modifier.weight(1f),
            )
            FactTile(
                label = stringResource(Res.string.history_workshop_code),
                value = workshop.code ?: NoCode,
                numeric = true,
                trailing = if (workshop.code != null) {
                    {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            tint = colors.blueText,
                            modifier = Modifier.size(CopyIcon),
                        )
                    }
                } else {
                    null
                },
                note = stringResource(Res.string.history_copied).takeIf { copied },
                // `Clipboard.setClipEntry` suspends, and the entry itself is per-platform — both
                // handled by the shared helper rather than here.
                onClick = workshop.code?.let { code ->
                    {
                        copyScope.launch { clipboard.setClipEntry(plainTextClipEntry(code)) }
                        copied = true
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }

        DashedDivider()

        workshop.months.forEach { worked ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = PersianDateFormatter.monthNames[worked.monthIndex],
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.width(MonthColumnWidth),
                )
                NumericText(
                    text = stringResource(
                        Res.string.history_combined_year_days,
                        worked.days.toString().toPersianDigits(),
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                Box(modifier = Modifier.weight(1f))
                WageText(
                    amount = worked.wage.toRialAmount(fallback = "")
                        .removeSuffix(RialSuffix)
                        .toPersianDigits(),
                    rialLabel = stringResource(Res.string.history_rial),
                    color = colors.textPrimary,
                )
            }
        }
    }
}

/** A labeled fact on its own tile — the branch, and the workshop number that can be copied. */
@Composable
private fun FactTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    note: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(TileCorner))
            .background(colors.bgPage)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = TilePaddingH, vertical = TilePaddingV),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            trailing?.invoke()
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            if (numeric) {
                NumericText(text = value, style = style, color = colors.textPrimary)
            } else {
                Text(text = value, style = style, color = colors.textPrimary, maxLines = 1)
            }
            note?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.greenText,
                )
            }
        }
    }
}

/** The design's dashed rule, drawn rather than assembled from a row of little boxes. */
@Composable
fun DashedDivider(modifier: Modifier = Modifier) {
    val color = LocalTaminColors.current.divider
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Hairline)
            .drawBehind {
                var x = 0f
                val dash = DashWidth.toPx()
                val gap = DashGap.toPx()
                while (x < size.width) {
                    drawRect(color, Offset(x, 0f), size.copy(width = dash))
                    x += dash + gap
                }
            },
    )
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    val brush = remember {
        Brush.linearGradient(listOf(TaminHistoryButtonStart, TaminHistoryButtonEnd))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.sm)
            .clip(RoundedCornerShape(ButtonCorner))
            .background(brush)
            .clickable(onClick = onClick)
            .height(ButtonHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.history_close),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
        )
    }
}

private const val SEASONS = 4

private val SeasonLabels = listOf(
    Res.string.history_combined_season_spring,
    Res.string.history_combined_season_summer,
    Res.string.history_combined_season_autumn,
    Res.string.history_combined_season_winter,
)

private val SeasonColors = listOf(
    TaminHistorySeasonSpring,
    TaminHistorySeasonSummer,
    TaminHistorySeasonAutumn,
    TaminHistorySeasonWinter,
)

/** Nothing recorded for this year, which is a legitimate answer rather than a failure. */
internal val NoWorkshops: ImmutableList<DastmozdInfoItemPR> = persistentListOf()

private const val FULL_YEAR_DAYS = 365
private const val EmptyMonth = "00"
private const val NoCode = "—"
private const val RialSuffix = " ریال"
private val SheetCorner = 30.dp
private val SheetPaddingH = 18.dp
private val SheetPaddingBottom = 22.dp
private val Hairline = 1.dp
private val PillCorner = 100.dp
private val ChipPaddingV = 4.dp
private val CardPaddingH = 12.dp
private val CardPaddingV = 10.dp
private val SeasonDot = 6.dp
private val TileCorner = 12.dp
private val TilePaddingH = 10.dp
private val TilePaddingV = 8.dp
private val CopyIcon = 12.dp
private val MonthColumnWidth = 52.dp
private val ButtonCorner = 15.dp
private val ButtonHeight = 46.dp
private val DashWidth = 4.dp
private val DashGap = 3.dp
