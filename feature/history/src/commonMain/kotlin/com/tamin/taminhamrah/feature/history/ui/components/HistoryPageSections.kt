package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.model.WorkshopPR
import com.tamin.taminhamrah.mapper.history.labelRes
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryInfoBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryInfoIcon
import com.tamin.taminhamrah.ui.theme.TaminHistoryInfoText
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.ic_tamin_download
import taminx.feature.history.Res
import taminx.feature.history.history_action_download_subtitle
import taminx.feature.history.history_action_download_title
import taminx.feature.history.history_action_send_subtitle
import taminx.feature.history.history_action_send_title
import taminx.feature.history.history_combined_year_days
import taminx.feature.history.history_download_sheet_title
import taminx.feature.history.history_many_shops_subtitle
import taminx.feature.history.history_many_shops_title
import taminx.feature.history.history_note_gaps
import taminx.feature.history.history_note_span
import taminx.core.core_ui.Res as CoreRes

/**
 * The span of a career in one sentence, gaps included.
 *
 * Rendered in the new calm cyan banner with an info icon.
 */
@Composable
fun HistorySpanNote(
    yearCount: Int,
    firstYear: String,
    lastYear: String,
    gapYears: Int,
    modifier: Modifier = Modifier,
) {
    val span = stringResource(
        Res.string.history_note_span,
        yearCount.toString().toPersianDigits(),
        firstYear.toPersianDigits(),
        lastYear.toPersianDigits(),
    )
    val gaps = if (gapYears > 0) {
        stringResource(Res.string.history_note_gaps, gapYears.toString().toPersianDigits())
    } else {
        HistoryConstants.SENTENCE_END
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TaminHistoryInfoBg)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = TaminHistoryInfoIcon,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = span + gaps,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.5.sp,
                lineHeight = 20.sp,
            ),
            color = TaminHistoryInfoText,
            modifier = Modifier.weight(1f),
        )
    }
}

/** One employer under the chart: who they were, and how much of the year they reported. */
@Composable
fun WorkshopSummaryRow(
    workshop: WorkshopPR,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(16.dp) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface)
            .border(HistoryDimens.hairline, Color(0xFFEEF1F6), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Blue tile icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(colors.blueBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = workshop.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = Color(0xFF0F172A),
                maxLines = 1,
            )
            Text(
                text = workshop.type + HistoryConstants.SEPARATOR + workshop.branch,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                ),
                color = Color(0xFF9DB2CE),
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        DaysPill(days = workshop.totalDays)

        Icon(
            imageVector = Icons.Filled.ChevronLeft,
            contentDescription = null,
            tint = Color(0xFF9DB2CE),
            modifier = Modifier.size(16.dp),
        )
    }
}

/** Multi-workshop banner card shown when a year has multiple employers. */
@Composable
fun ManyWorkshopsBanner(
    workshopCount: Int,
    totalDays: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = remember { RoundedCornerShape(18.dp) }
    val gradient = remember {
        Brush.linearGradient(listOf(TaminHistoryButtonStart, TaminHistoryButtonEnd))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(gradient)
            .border(HistoryDimens.hairline, Color(0x29FFFFFF), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = Color(0xFF173D7E),
                modifier = Modifier.size(20.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.history_many_shops_title, workshopCount.toString().toPersianDigits()),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = Color.White,
            )
            Text(
                text = stringResource(Res.string.history_many_shops_subtitle),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                ),
                color = Color(0xC2FFFFFF),
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0x2EFFFFFF))
                .border(HistoryDimens.hairline, Color(0x42FFFFFF), CircleShape)
                .padding(horizontal = 9.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(Res.string.history_combined_year_days, totalDays.toString().toPersianDigits()),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = Color.White,
            )
        }

        Icon(
            imageVector = Icons.Filled.ChevronLeft,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp),
        )
    }
}

/** «۲۴۵ روز» — the pill used in workshop summary rows. */
@Composable
fun DaysPill(days: Int, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.blueBg)
            .padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
        Text(
            text = stringResource(
                Res.string.history_combined_year_days,
                days.toString().toPersianDigits(),
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = colors.blueText,
        )
    }
}

/** The two action cards: Download PDF and Send to Institutions. */
@Composable
fun HistoryActionCards(
    onDownload: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        ActionCardItem(
            icon = vectorResource(CoreRes.drawable.ic_tamin_download),
            iconBg = colors.blueBg,
            iconTint = colors.blueText,
            title = stringResource(Res.string.history_action_download_title),
            subtitle = stringResource(Res.string.history_action_download_subtitle),
            onClick = onDownload,
            modifier = Modifier.weight(1f),
        )
        ActionCardItem(
            icon = Icons.AutoMirrored.Filled.Send,
            iconBg = colors.tealBg,
            iconTint = colors.tealText,
            title = stringResource(Res.string.history_action_send_title),
            subtitle = stringResource(Res.string.history_action_send_subtitle),
            onClick = onSend,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionCardItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = remember { RoundedCornerShape(18.dp) }
    Column(
        modifier = modifier
            .clip(shape)
            .background(Color.White)
            .border(HistoryDimens.hairline, Color(0xFFEEF1F6), shape)
            .clickable(onClick = onClick)
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            ),
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(top = 3.dp),
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                lineHeight = 16.sp,
            ),
            color = Color(0xFF9DB2CE),
        )
    }
}

/**
 * The three reports, in the order the previous app listed them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportMenuSheet(
    onSelect: (HistoryCertificateType) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.history_download_sheet_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
            )

            ReportTypes.forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(colors.bgSurface)
                        .border(HistoryDimens.hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
                        .clickable { onSelect(type) }
                        .padding(horizontal = HistoryDimens.rowPaddingH, vertical = HistoryDimens.rowPaddingV),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(HistoryDimens.tileSize)
                            .clip(RoundedCornerShape(HistoryDimens.tileCorner))
                            .background(colors.blueBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = colors.blueText,
                            modifier = Modifier.size(HistoryDimens.tileIconSize),
                        )
                    }
                    Text(
                        text = stringResource(type.labelRes()),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = vectorResource(CoreRes.drawable.ic_tamin_download),
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(HistoryDimens.chevronSize),
                    )
                }
            }
        }
    }
}

/** Declared beside the sheet that orders them, so the order is not a coincidence of the enum. */
private val ReportTypes = listOf(
    HistoryCertificateType.ALL,
    HistoryCertificateType.WAGES,
    HistoryCertificateType.COMBINED,
)
