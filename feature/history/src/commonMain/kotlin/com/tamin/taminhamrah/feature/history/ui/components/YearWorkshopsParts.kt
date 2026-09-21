package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.HistoryFixtures
import com.tamin.taminhamrah.feature.history.ui.model.WorkshopPR
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.feature.history.ui.model.detailWith
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.plainTextClipEntry
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
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
import taminx.feature.history.history_combined_season_autumn
import taminx.feature.history.history_combined_season_spring
import taminx.feature.history.history_combined_season_summer
import taminx.feature.history.history_combined_season_winter
import taminx.feature.history.history_combined_year_days
import taminx.feature.history.history_copied
import taminx.feature.history.history_rial
import taminx.feature.history.history_workshop_branch
import taminx.feature.history.history_workshop_code

/** Three months under a colored dot — the season, as the design groups a year. */
@Composable
internal fun SeasonCard(detail: YearDetailPR, season: Int, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val firstMonth = season * HistoryConstants.MONTHS_PER_SEASON

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(HistoryDimens.hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = HistoryDimens.seasonCardPaddingH, vertical = HistoryDimens.seasonCardPaddingV),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(HistoryDimens.seasonDot)
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

        for (offset in 0 until HistoryConstants.MONTHS_PER_SEASON) {
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
                        HistoryConstants.EMPTY_MONTH.toPersianDigits()
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
internal fun WorkshopCard(workshop: WorkshopPR) {
    val colors = LocalTaminColors.current
    val clipboard = LocalClipboard.current
    val copyScope = rememberCoroutineScope()
    var copied by remember(workshop.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(HistoryDimens.hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
            .padding(HistoryDimens.seasonCardPaddingH),
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
                value = workshop.code ?: HistoryConstants.NO_CODE,
                numeric = true,
                trailing = if (workshop.code != null) {
                    {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            tint = colors.blueText,
                            modifier = Modifier.size(HistoryDimens.copyIcon),
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
                    modifier = Modifier.width(HistoryDimens.monthColumnWidth),
                )
                // «۱۲ روز» is a number *and a word*, so it stays in the page's own direction —
                // NumericText would force it left-to-right and print «روز ۱۲».
                Text(
                    text = stringResource(
                        Res.string.history_combined_year_days,
                        worked.days.toString().toPersianDigits(),
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                )
                Box(modifier = Modifier.weight(1f))
                WageText(
                    amount = worked.wage.toRialAmount(fallback = "")
                        .removeSuffix(HistoryConstants.RIAL_SUFFIX)
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
            .clip(RoundedCornerShape(HistoryDimens.tileCorner))
            .background(colors.bgPage)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = HistoryDimens.factTilePaddingH, vertical = HistoryDimens.factTilePaddingV),
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
            .height(HistoryDimens.hairline)
            .drawBehind {
                var x = 0f
                val dash = HistoryDimens.dashWidth.toPx()
                val gap = HistoryDimens.dashGap.toPx()
                while (x < size.width) {
                    drawRect(color, Offset(x, 0f), size.copy(width = dash))
                    x += dash + gap
                }
            },
    )
}

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

/** The pieces «کارگاه‌های سال» is built from, drawn on their own. */
@PreviewRtlTheme
@Composable
private fun YearWorkshopsPartsPreview() {
    PreviewRtlThemeContent {
        Column(modifier = Modifier.padding(HistoryDimens.sheetPaddingH)) {
            val detail = HistoryFixtures.years[2].detailWith(
                rows = HistoryFixtures.wageRows,
                optionalSchemeName = "بیمهٔ اختیاری",
                constructionSchemeName = "بیمهٔ کارگران ساختمانی",
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                SeasonCard(detail = detail, season = 0, modifier = Modifier.weight(1f))
                SeasonCard(detail = detail, season = 1, modifier = Modifier.weight(1f))
            }
            WorkshopCard(workshop = detail.workshops.first())
        }
    }
}
