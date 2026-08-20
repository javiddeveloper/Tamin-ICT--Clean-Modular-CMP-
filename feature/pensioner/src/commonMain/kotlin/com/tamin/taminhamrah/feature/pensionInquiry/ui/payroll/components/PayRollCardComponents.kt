package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollUiState
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.shrinkOnCollapse
import com.tamin.taminhamrah.ui.components.vanishOnCollapse
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.payroll_deduction_legend
import taminx.core.core_ui.payroll_deductions_section_title
import taminx.core.core_ui.payroll_empty_desc
import taminx.core.core_ui.payroll_empty_title
import taminx.core.core_ui.payroll_net_amount_label
import taminx.core.core_ui.payroll_net_label_template
import taminx.core.core_ui.payroll_paid_legend
import taminx.core.core_ui.payroll_payments_section_title
import taminx.core.core_ui.unit_rial

private const val CLP_TYPE_PAYMENT = "1"
private const val CLP_TYPE_DEDUCTION = "2"

/** How much the net-amount headline shrinks once the card is fully collapsed. */
private const val AMOUNT_COLLAPSED_SCALE = 0.72f

// ─── Main amount card ──────────────────────────────────────────────────────────

private enum class PayRollCardSlot { LabelHint, DateLabel, Amount, ProgressBar, Legend }

private fun List<Measurable>.slot(id: PayRollCardSlot): Measurable = first { it.layoutId == id }

/**
 * The primary payroll net-amount card. [collapseProgress] drives a 0→1 morph — the same
 * shrinking-[Layout] mechanism as `EdictMainCard` — that:
 * – fades out the legend
 * – rides the "خالص پرداختی <نوع>" label to the compact bar (pinned to the start edge the whole
 *   way), with the amount traveling in to land right beside it — exactly Edict's date-label +
 *   amount pairing
 * – rides the date label (pinned to the end edge, playing the role Edict's percent badge plays)
 *   and the progress bar into the compact bar too
 *
 * A plain `Card` + `Column` here (as opposed to this `Layout`) would keep laying out at its full
 * expanded height regardless of [collapseProgress] — `vanishOnCollapse` only fades content, it
 * doesn't shrink the space it occupies — so the pinned header would never actually get shorter
 * and would permanently cover the body content scrolling underneath it.
 */
@Composable
fun PayRollMainCard(
    state: PayRollUiState,
    collapseProgress: () -> Float = { 0f },
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val items = state.payRollList
    val paidTotal = items.filter { it.clpType == CLP_TYPE_PAYMENT }.sumOf { it.sumAmount }
    val deductionTotal = items.filter { it.clpType == CLP_TYPE_DEDUCTION }.sumOf { it.sumAmount }
    val netTotal = paidTotal + deductionTotal
    val rial = stringResource(Res.string.unit_rial)
    val dateLabelText = if (state.startDate.isNotEmpty()) formatPayRollDateLabel(state.startDate) else ""
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
    ) {
        Layout(
            content = {
                // LabelHint: "خالص پرداختی <نوع>" – stays pinned to the start edge; the amount
                // travels in to sit right beside it in the compact bar, exactly like Edict's date
                // label + amount pairing.
                TaminText(
                    text = stringResource(
                        Res.string.payroll_net_label_template,
                        payRollPaymentTypeLabel(state.paymentType),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                    modifier = Modifier.layoutId(PayRollCardSlot.LabelHint),
                )
                // DateLabel: stays pinned at the end edge, rides vertically into the compact bar
                TaminText(
                    text = dateLabelText,
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                    modifier = Modifier.layoutId(PayRollCardSlot.DateLabel),
                )
                // Amount: big net number + unit – travels beside LabelHint in the compact bar,
                // shrinking (visually scaled, not re-measured, so the collapse costs no
                // recomposition) once collapsing so the big headline figure doesn't crowd the bar.
                Row(
                    modifier = Modifier
                        .layoutId(PayRollCardSlot.Amount)
                        .shrinkOnCollapse(
                            progress = collapseProgress,
                            minScale = AMOUNT_COLLAPSED_SCALE,
                            rtl = rtl,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TaminText(
                        text = netTotal.toPriceFormat(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary,
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    TaminText(
                        text = rial,
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textSecondary,
                    )
                }
                // ProgressBar – rides up into the compact bar
                PayRollProgressBar(
                    paidTotal = paidTotal,
                    deductionTotal = deductionTotal,
                    modifier = Modifier.layoutId(PayRollCardSlot.ProgressBar).fillMaxWidth(),
                )
                // Legend: paid/deduction totals – vanishes on collapse
                Column(
                    modifier = Modifier
                        .layoutId(PayRollCardSlot.Legend)
                        .vanishOnCollapse(collapseProgress),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    PayRollLegendItem(
                        label = stringResource(Res.string.payroll_paid_legend),
                        amount = paidTotal,
                        color = taminColors.blueText,
                        rial = rial,
                    )
                    PayRollLegendItem(
                        label = stringResource(Res.string.payroll_deduction_legend),
                        amount = deductionTotal,
                        color = taminColors.orangeText,
                        rial = rial,
                    )
                }
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val pad = Spacing.lg.roundToPx()
            val sm = Spacing.sm.roundToPx()
            val lg = Spacing.lg.roundToPx()
            val xs = Spacing.xs.roundToPx()
            val innerC = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))

            val labelHint = measurables.slot(PayRollCardSlot.LabelHint).measure(innerC)
            val dateLabel = measurables.slot(PayRollCardSlot.DateLabel).measure(innerC)
            val amount = measurables.slot(PayRollCardSlot.Amount).measure(innerC)
            val progressBar = measurables.slot(PayRollCardSlot.ProgressBar)
                .measure(Constraints.fixedWidth((width - 2 * pad).coerceAtLeast(0)))
            val legend = measurables.slot(PayRollCardSlot.Legend).measure(innerC)

            // Expanded geometry
            val row1Height = maxOf(labelHint.height, dateLabel.height)
            val row1Top = pad
            val amountTop = row1Top + row1Height + sm
            val progressTop = amountTop + amount.height + lg
            val legendTop = progressTop + progressBar.height + lg
            val expandedH = legendTop + legend.height + pad

            // Collapsed geometry: compact bar = [LabelHint Amount ... DateLabel], then ProgressBar
            val barRowHeight = maxOf(labelHint.height, amount.height, dateLabel.height)
            val collapsedH = pad + barRowHeight + sm + progressBar.height + pad

            val labelHintColY = pad + (barRowHeight - labelHint.height) / 2
            val amountColY = pad + (barRowHeight - amount.height) / 2
            val dateLabelColY = pad + (barRowHeight - dateLabel.height) / 2
            val progressBarColY = pad + barRowHeight + sm

            val t = Easing.standard.transform(collapseProgress())

            layout(width, lerp(expandedH, collapsedH, t)) {
                // Legend fades in place (clipped as the card shrinks).
                legend.placeRelative(pad, legendTop)

                // Traveling pieces glide from expanded → compact bar positions.
                // LabelHint: pinned to the start edge throughout, rides vertically from row 1 to
                // the bar — the amount travels in beside it, not the other way around.
                labelHint.placeRelative(
                    pad,
                    lerp(row1Top + (row1Height - labelHint.height) / 2, labelHintColY, t),
                )
                // DateLabel: pinned to the end edge, rides vertically from row 1 to the bar.
                dateLabel.placeRelative(
                    width - pad - dateLabel.width,
                    lerp(row1Top + (row1Height - dateLabel.height) / 2, dateLabelColY, t),
                )
                // Amount: below row 1 (expanded) → right beside LabelHint in the compact bar.
                amount.placeRelative(
                    lerp(pad, pad + labelHint.width + xs, t),
                    lerp(amountTop, amountColY, t),
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
private fun PayRollProgressBar(paidTotal: Long, deductionTotal: Long, modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    val total = paidTotal + kotlin.math.abs(deductionTotal)

    Row(
        modifier = modifier.height(8.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (total > 0) {
            if (paidTotal > 0) {
                Box(
                    modifier = Modifier
                        .weight(paidTotal.toFloat() / total)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(taminColors.blueText),
                )
            }
            if (deductionTotal != 0L) {
                Box(
                    modifier = Modifier
                        .weight(kotlin.math.abs(deductionTotal).toFloat() / total)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(taminColors.orangeText),
                )
            }
        }
    }
}

@Composable
private fun PayRollLegendItem(label: String, amount: Long, color: Color, rial: String) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(Spacing.sm))
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        TaminText(
            text = "${amount.toPriceFormat()} $rial",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary,
        )
    }
}

// ─── Breakdown sections ─────────────────────────────────────────────────────────

@Composable
fun PayRollBreakdownSections(items: List<PayRollPR>, modifier: Modifier = Modifier) {
    val payments = items.filter { it.clpType == CLP_TYPE_PAYMENT }
    val deductions = items.filter { it.clpType == CLP_TYPE_DEDUCTION }
    val taminColors = LocalTaminColors.current
    val rial = stringResource(Res.string.unit_rial)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        if (payments.isNotEmpty()) {
            PayRollSectionCard(
                title = stringResource(Res.string.payroll_payments_section_title),
                totalLabelColor = taminColors.blueText,
                total = payments.sumOf { it.sumAmount },
                items = payments,
                rial = rial,
            )
        }
        if (deductions.isNotEmpty()) {
            PayRollSectionCard(
                title = stringResource(Res.string.payroll_deductions_section_title),
                totalLabelColor = taminColors.orangeText,
                total = deductions.sumOf { it.sumAmount },
                items = deductions,
                rial = rial,
            )
        }
    }
}

@Composable
private fun PayRollSectionCard(
    title: String,
    totalLabelColor: Color,
    total: Long,
    items: List<PayRollPR>,
    rial: String,
) {
    val taminColors = LocalTaminColors.current
    var expanded by remember { mutableStateOf(true) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "payroll-section-chevron",
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = BorderStroke(1.dp, taminColors.border),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(totalLabelColor))
                    Spacer(Modifier.width(Spacing.sm))
                    TaminText(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TaminText(
                        text = "${total.toPriceFormat()} $rial",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = totalLabelColor,
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = taminColors.textSecondary,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { rotationZ = chevronRotation },
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(Spacing.md))
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            TaminText(
                                text = item.tprDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = taminColors.textSecondary,
                                modifier = Modifier.weight(1f),
                            )
                            TaminText(
                                text = "${item.sumAmount.toPriceFormat()} $rial",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = taminColors.textPrimary,
                            )
                        }
                        if (index < items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.sm),
                                color = taminColors.divider,
                                thickness = 0.5.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Net summary bar ─────────────────────────────────────────────────────────

@Composable
fun PayRollNetSummaryBar(items: List<PayRollPR>, modifier: Modifier = Modifier) {
    val net = items.sumOf { it.sumAmount }
    val rial = stringResource(Res.string.unit_rial)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LocalTaminColors.current.blueText),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TaminText(
                text = stringResource(Res.string.payroll_net_amount_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            TaminText(
                text = "${net.toPriceFormat()} $rial",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────

@Composable
fun PayRollEmptyCard(onShowAll: () -> Unit, modifier: Modifier = Modifier) {
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
                text = stringResource(Res.string.payroll_empty_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                textAlign = TextAlign.Center,
            )
            TaminText(
                text = stringResource(Res.string.payroll_empty_desc),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollEmptyCardPreview() {
    PreviewRtlThemeContent {
        PayRollEmptyCard(onShowAll = {}, modifier = Modifier.padding(Spacing.lg))
    }
}
