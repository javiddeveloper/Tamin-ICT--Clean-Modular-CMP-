package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.components.vanishOnCollapse
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.model.pension.EdictPensionerDetailPR
import com.tamin.taminhamrah.model.pension.EdictPensionerPR
import com.tamin.taminhamrah.model.pension.SurvivorInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminColors
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.edict_comparison_after
import taminx.core.core_ui.edict_comparison_before
import taminx.core.core_ui.edict_comparison_title
import taminx.core.core_ui.edict_description_title
import taminx.core.core_ui.edict_payable_monthly_hint
import taminx.core.core_ui.edict_survivor_desc
import taminx.core.core_ui.edict_survivor_share_title
import taminx.core.core_ui.edict_tab_breakdown
import taminx.core.core_ui.edict_tab_info
import taminx.core.core_ui.edict_empty_desc
import taminx.core.core_ui.edict_empty_show_all
import taminx.core.core_ui.edict_empty_title
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.unit_rial

// ─── Main Amount Card ──────────────────────────────────────────────────────────

private enum class EdictCardSlot { LabelHint, DateLabel, Badge, Amount, ProgressBar, Legend }

private fun List<Measurable>.slot(id: EdictCardSlot): Measurable = first { it.layoutId == id }

/**
 * The primary edict amount card. [collapseProgress] drives a 0→1 morph that:
 * – fades out the "مبلغ قابل پرداخت ماهانه" hint, the percent badge, and the legend
 * – slides the date label to the start edge and the amount to the end edge of a compact bar
 * – rides the progress bar up just below that bar
 */
@Composable
fun EdictMainCard(
    edict: EdictPensionerPR,
    collapseProgress: () -> Float = { 0f },
    modifier: Modifier = Modifier,
    selectedDate: String = "",
) {
    val taminColors = LocalTaminColors.current
    val info = edict.edictInfo ?: return

    val beforeAmt = info.pensionBeforeIncrease.replace(",", "").toDoubleOrNull() ?: 0.0
    val afterAmt = info.pensionAfterIncrease.replace(",", "").toDoubleOrNull() ?: 0.0
    val pct = if (beforeAmt > 0) ((afterAmt - beforeAmt) / beforeAmt * 100).toInt() else 0
    val rawDate = formatEdictDateLabel(selectedDate)
    val dateLabelText = if (rawDate.isNotEmpty()) " \u00B7 $rawDate" else ""

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
    ) {
        Layout(
            content = {
                // LabelHint: "مبلغ قابل پرداخت ماهانه" – vanishes on collapse
                TaminText(
                    text = stringResource(Res.string.edict_payable_monthly_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                    modifier = Modifier
                        .layoutId(EdictCardSlot.LabelHint)
                        .vanishOnCollapse(collapseProgress),
                )
                // DateLabel: "فروردین ۱۴۰۰" – travels from beside the hint to the start edge
                TaminText(
                    text = dateLabelText,
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                    modifier = Modifier.layoutId(EdictCardSlot.DateLabel),
                )
                // Badge: PercentBadge – stays visible, rides vertically into the compact bar
                Box(modifier = Modifier.layoutId(EdictCardSlot.Badge)) {
                    if (pct > 0) PercentBadge(pct, taminColors)
                }
                // Amount: big payable number + unit – travels to the end edge of the bar
                Row(
                    modifier = Modifier.layoutId(EdictCardSlot.Amount),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    TaminText(
                        text = info.payableMonthly,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary,
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    TaminText(
                        text = stringResource(Res.string.unit_rial),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textSecondary,
                    )
                }
                // ProgressBar – rides up into the compact bar
                EdictBreakdownProgressBar(
                    details = edict.detail.take(4),
                    modifier = Modifier.layoutId(EdictCardSlot.ProgressBar),
                )
                // Legend: breakdown items – vanishes on collapse
                Column(
                    modifier = Modifier
                        .layoutId(EdictCardSlot.Legend)
                        .vanishOnCollapse(collapseProgress),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    edict.detail.take(4).forEachIndexed { index, detail ->
                        BreakdownLegendItem(detail = detail, index = index)
                    }
                }
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val pad = Spacing.lg.roundToPx()
            val sm = Spacing.sm.roundToPx()
            val lg = Spacing.lg.roundToPx()
            val xs = Spacing.xs.roundToPx()
            val innerC = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))

            val labelHint = measurables.slot(EdictCardSlot.LabelHint).measure(innerC)
            val dateLabel = measurables.slot(EdictCardSlot.DateLabel).measure(innerC)
            val badge = measurables.slot(EdictCardSlot.Badge).measure(Constraints())
            val amount = measurables.slot(EdictCardSlot.Amount).measure(innerC)
            val progressBar = measurables.slot(EdictCardSlot.ProgressBar)
                .measure(Constraints.fixedWidth((width - 2 * pad).coerceAtLeast(0)))
            val legend = measurables.slot(EdictCardSlot.Legend).measure(innerC)

            // Expanded geometry
            val row1Height = maxOf(labelHint.height, dateLabel.height, badge.height)
            val row1Top = pad
            val amountTop = row1Top + row1Height + sm
            val progressTop = amountTop + amount.height + lg
            val legendTop = progressTop + progressBar.height + lg
            val expandedH = legendTop + legend.height + pad

            // Collapsed geometry: compact bar = [DateLabel | Amount], then ProgressBar
            val barRowHeight = maxOf(dateLabel.height, amount.height)
            val collapsedH = pad + barRowHeight + sm + progressBar.height + pad

            val dateLabelColY = pad + (barRowHeight - dateLabel.height) / 2
            val amountColY = pad + (barRowHeight - amount.height) / 2
            val progressBarColY = pad + barRowHeight + sm

            val t = Easing.standard.transform(collapseProgress())

            layout(width, lerp(expandedH, collapsedH, t)) {
                // Fading pieces stay at their expanded slots (clipped as the card shrinks).
                labelHint.placeRelative(
                    pad,
                    row1Top + (row1Height - labelHint.height) / 2,
                )
                legend.placeRelative(pad, legendTop)

                // Traveling pieces glide from expanded → compact bar positions.
                // DateLabel: beside the hint (expanded) → start edge of bar (collapsed).
                dateLabel.placeRelative(
                    lerp(pad + labelHint.width + xs, pad, t),
                    lerp(row1Top + (row1Height - dateLabel.height) / 2, dateLabelColY, t),
                )
                // Amount: below the hint row (expanded) → right beside DateLabel in collapsed bar.
                amount.placeRelative(
                    lerp(pad, pad + dateLabel.width + xs, t),
                    lerp(amountTop, amountColY, t),
                )
                // Badge: stays at the end edge, rides vertically from row 1 center to bar center.
                badge.placeRelative(
                    width - pad - badge.width,
                    lerp(
                        row1Top + (row1Height - badge.height) / 2,
                        pad + (barRowHeight - badge.height) / 2,
                        t,
                    ),
                )
                // ProgressBar: below the amount (expanded) → just below the bar (collapsed).
                progressBar.placeRelative(
                    pad,
                    lerp(progressTop, progressBarColY, t),
                )
            }
        }
    }
}

@Composable
private fun PercentBadge(percent: Int, colors: TaminColors) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.sm))
            .background(colors.greenBg)
            .padding(horizontal = Spacing.sm, vertical = 2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TaminText(
                text = "${percent}٪",
                color = colors.greenText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_down),
                contentDescription = null,
                tint = colors.greenText,
                modifier = Modifier.size(12.dp).rotate(180f),
            )
        }
    }
}

@Composable
fun EdictBreakdownProgressBar(details: List<EdictPensionerDetailPR>, modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    val total = details.sumOf { it.fieldValue.replace(",", "").toDoubleOrNull() ?: 0.0 }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        if (total > 0) {
            details.forEachIndexed { index, detail ->
                val value = detail.fieldValue.replace(",", "").toDoubleOrNull() ?: 0.0
                if (value > 0) {
                    Box(
                        modifier = Modifier
                            .weight((value / total).toFloat())
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(getBreakdownColor(index, taminColors)),
                    )
                }
            }
        }
    }
}

@Composable
fun BreakdownLegendItem(detail: EdictPensionerDetailPR, index: Int) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(getBreakdownColor(index, taminColors)),
        )
        Spacer(Modifier.width(Spacing.sm))
        TaminText(
            text = detail.fieldDesc,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Spacing.sm))
        TaminText(
            text = detail.fieldValue,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary,
        )
    }
}

// ─── Comparison Card ───────────────────────────────────────────────────────────

@Composable
fun EdictComparisonCard(edict: EdictPensionerPR) {
    val taminColors = LocalTaminColors.current
    val info = edict.edictInfo ?: return

    val beforeAmt = info.pensionBeforeIncrease.replace(",", "").toDoubleOrNull() ?: 0.0
    val afterAmt = info.pensionAfterIncrease.replace(",", "").toDoubleOrNull() ?: 0.0
    val maxAmt = maxOf(beforeAmt, afterAmt).takeIf { it > 0 } ?: 1.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = BorderStroke(1.dp, taminColors.border),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = stringResource(Res.string.edict_comparison_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                if (edict.edictMonth.isNotEmpty() && edict.edictYear.isNotEmpty()) {
                    TaminText(
                        text = formatEdictDateLabel(edict.edictYear + edict.edictMonth.padStart(2, '0')),
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textMuted,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.md))

            ComparisonBar(
                label = stringResource(Res.string.edict_comparison_before),
                amount = info.pensionBeforeIncrease,
                progress = (beforeAmt / maxAmt).toFloat(),
                color = taminColors.textMuted,
            )
            Spacer(Modifier.height(Spacing.md))
            ComparisonBar(
                label = stringResource(Res.string.edict_comparison_after),
                amount = info.pensionAfterIncrease,
                progress = (afterAmt / maxAmt).toFloat(),
                color = taminColors.teal,
            )

            if (info.edictDescription.isNotEmpty()) {
                Spacer(Modifier.height(Spacing.md))
                TaminText(
                    text = info.edictDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun ComparisonBar(label: String, amount: String, progress: Float, color: Color) {
    val taminColors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TaminText(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textSecondary,
            )
            TaminText(
                text = amount,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(taminColors.divider),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

// ─── Survivor Share Card ───────────────────────────────────────────────────────

@Composable
fun SurvivorShareCard(survivor: SurvivorInfoPR) {
    val taminColors = LocalTaminColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = BorderStroke(1.dp, taminColors.border),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TaminText(
                    text = stringResource(
                        Res.string.edict_survivor_share_title,
                        "${survivor.firstName} ${survivor.lastName}",
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(Spacing.xs))
                TaminText(
                    text = stringResource(Res.string.edict_survivor_desc, survivor.lastName),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                    lineHeight = 18.sp,
                )
            }

            Spacer(Modifier.width(Spacing.md))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                CircularProgressIndicator(
                    progress = { (survivor.quota.toFloatOrNull() ?: 0f) / 100f },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 6.dp,
                    color = taminColors.teal,
                    trackColor = taminColors.divider,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                )
                TaminText(
                    text = "${survivor.quota}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.blueText,
                )
            }
        }
    }
}

// ─── Details Section (Tabs) ────────────────────────────────────────────────────

@Composable
fun EdictDetailsSection(edict: EdictPensionerPR) {
    val taminColors = LocalTaminColors.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(Res.string.edict_tab_info),
        stringResource(Res.string.edict_tab_breakdown),
    )

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(taminColors.divider)
                .padding(4.dp),
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedTabIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .background(if (isSelected) taminColors.bgSurface else Color.Transparent)
                        .clickable { selectedTabIndex = index }
                        .padding(vertical = Spacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    TaminText(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) taminColors.blueText else taminColors.textSecondary,
                    )
                }
            }
        }

        if (selectedTabIndex == 0) EdictInfoList(edict) else EdictBreakdownList(edict)
    }
}

@Composable
private fun EdictInfoList(edict: EdictPensionerPR) {
    val taminColors = LocalTaminColors.current
    val info = edict.edictInfo ?: return

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.card),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = BorderStroke(1.dp, taminColors.border),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                InfoRow(label = "شعبه", value = edict.branchName)
                InfoRow(label = "نام و نام خانوادگی", value = "${info.firstName} ${info.lastName}")
                InfoRow(label = "نام پدر", value = info.fatherName)
                InfoRow(label = "اساس برقراری", value = info.basisImplementation)
                InfoRow(label = "تاریخ برقراری", value = info.pensionStartDate)
                InfoRow(
                    label = "سابقه اصلی",
                    value = "${info.originalHistoryYear} سال و ${info.originalHistoryMonth} ماه و ${info.originalHistoryDay} روز",
                )
                InfoRow(
                    label = "سابقه ارفاقی",
                    value = "${info.additionalYear} سال و ${info.additionalMonth} ماه و ${info.additionalDay} روز",
                )
                InfoRow(label = "نوع حکم", value = edict.title)
            }
        }

        if (info.edictDescription.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.card),
                colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                border = BorderStroke(1.dp, taminColors.border),
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    TaminText(
                        text = stringResource(Res.string.edict_description_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(Spacing.sm))
                    TaminText(
                        text = info.edictDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textSecondary,
                        lineHeight = 20.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    val taminColors = LocalTaminColors.current
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TaminText(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textSecondary,
            )
            TaminText(
                text = (value ?: ""),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = Spacing.md),
            )
        }
        HorizontalDivider(color = taminColors.divider, thickness = 0.5.dp)
    }
}

@Composable
private fun EdictBreakdownList(edict: EdictPensionerPR) {
    val taminColors = LocalTaminColors.current
    val info = edict.edictInfo

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = BorderStroke(1.dp, taminColors.border),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(taminColors.blueText),
                )
                Spacer(Modifier.width(Spacing.sm))
                TaminText(
                    text = "مبالغ پرداختی",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(Spacing.md))

            edict.detail.forEachIndexed { index, detail ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TaminText(
                        text = detail.fieldDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    TaminText(
                        text = detail.fieldValue + " " + stringResource(Res.string.unit_rial),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (index < edict.detail.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.sm),
                        color = taminColors.divider,
                        thickness = 0.5.dp,
                    )
                }
            }

            if (info != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Spacing.md),
                    color = taminColors.divider,
                    thickness = 1.dp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TaminText(
                        text = "جمع کل حکم",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    TaminText(
                        text = info.payableMonthly + " " + stringResource(Res.string.unit_rial),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.blueText,
                    )
                }
            }
        }
    }
}

// ─── Helpers ───────────────────────────────────────────────────────────────────

fun getBreakdownColor(index: Int, colors: TaminColors): Color = when (index % 4) {
    0 -> colors.orangeText
    1 -> colors.teal
    2 -> colors.blueText
    else -> colors.fuchsiaBlue
}

// ─── Empty State Card ──────────────────────────────────────────────────────────

@Composable
fun EdictEmptyCard(
    onShowAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(taminColors.blueBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_tamin_search),
                    contentDescription = null,
                    tint = taminColors.blueText,
                    modifier = Modifier.size(28.dp),
                )
            }
            TaminText(
                text = stringResource(Res.string.edict_empty_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                textAlign = TextAlign.Center,
            )
            TaminText(
                text = stringResource(Res.string.edict_empty_desc),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun EdictDetailsSectionPreview() {
    PreviewRtlThemeContent {
        // Preview with empty edict (no data guard)
    }
}

@PreviewRtlTheme
@Composable
private fun EdictEmptyCardPreview() {
    PreviewRtlThemeContent {
        EdictEmptyCard(
            onShowAll = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
