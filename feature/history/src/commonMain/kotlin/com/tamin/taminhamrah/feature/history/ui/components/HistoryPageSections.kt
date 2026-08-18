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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.history.ui.model.WorkshopPR
import com.tamin.taminhamrah.mapper.history.labelRes
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.TaminActionTile
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminTeal900
import com.tamin.taminhamrah.ui.theme.TaminTealBg
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_action_download_subtitle
import taminx.feature.history.history_action_download_title
import taminx.feature.history.history_action_send_subtitle
import taminx.feature.history.history_action_send_title
import taminx.feature.history.history_combined_year_days
import taminx.feature.history.history_download_sheet_title
import taminx.feature.history.history_note_gaps
import taminx.feature.history.history_note_span

/**
 * The span of a career in one sentence, gaps included.
 *
 * Uses the shared banner in its calm teal form: nothing here is a warning, it is the shape of the
 * years the service holds — and the years it does not.
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
        SentenceEnd
    }

    BannerCard(message = span + gaps, type = BannerType.Tip, modifier = modifier)
}

/** One employer under the chart: who they were, and how much of the year they reported. */
@Composable
fun WorkshopSummaryRow(
    workshop: WorkshopPR,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(Hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = RowPaddingH, vertical = RowPaddingV),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WorkshopIconTile()

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = workshop.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
                maxLines = 1,
            )
            Text(
                text = workshop.type + Separator + workshop.branch,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                maxLines = 1,
            )
        }

        DaysPill(days = workshop.totalDays)

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(ChevronSize),
        )
    }
}

@Composable
private fun WorkshopIconTile() {
    val colors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .size(TileSize)
            .clip(RoundedCornerShape(TileCorner))
            .background(colors.blueBg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(TileIconSize),
        )
    }
}

/** «۲۴۵ روز» — the same pill the workshop rows and the sheet header both use. */
@Composable
fun DaysPill(days: Int, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(PillCorner))
            .background(colors.blueBg)
            .padding(horizontal = Spacing.sm, vertical = PillPaddingV),
    ) {
        Text(
            text = stringResource(
                Res.string.history_combined_year_days,
                days.toString().toPersianDigits(),
            ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.blueText,
        )
    }
}

/** The two things this page can do with the history it just showed. */
@Composable
fun HistoryActionCards(
    onDownload: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        TaminActionTile(
            icon = Icons.Filled.Download,
            title = stringResource(Res.string.history_action_download_title),
            subtitle = stringResource(Res.string.history_action_download_subtitle),
            iconTint = colors.blueText,
            iconBackground = colors.blueBg,
            onClick = onDownload,
            modifier = Modifier.weight(1f),
        )
        TaminActionTile(
            icon = Icons.AutoMirrored.Filled.Send,
            title = stringResource(Res.string.history_action_send_title),
            subtitle = stringResource(Res.string.history_action_send_subtitle),
            iconTint = TaminTeal900,
            iconBackground = TaminTealBg,
            onClick = onSend,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * The three reports, in the order the previous app listed them.
 *
 * A plain list rather than the shared selection sheet: choosing here opens the file straight away,
 * so there is nothing to confirm and no submit button to press.
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
                        .border(Hairline, colors.border, RoundedCornerShape(CornerRadius.lg))
                        .clickable { onSelect(type) }
                        .padding(horizontal = RowPaddingH, vertical = RowPaddingV),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WorkshopIconTile()
                    Text(
                        text = stringResource(type.labelRes()),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(ChevronSize),
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

private const val Separator = " · "
private const val SentenceEnd = "."
private val Hairline = 1.dp
private val RowPaddingH = 12.dp
private val RowPaddingV = 11.dp
private val TileSize = 34.dp
private val TileCorner = 12.dp
private val TileIconSize = 18.dp
private val ChevronSize = 16.dp
private val PillCorner = 100.dp
private val PillPaddingV = 4.dp
