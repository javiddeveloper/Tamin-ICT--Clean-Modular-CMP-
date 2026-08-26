package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentUiState
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.collapseAway
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.components.shrinkOnCollapse
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.edict_search_title
import taminx.core.core_ui.ic_inbox
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.ic_tamin_shield_check
import taminx.core.core_ui.inspection_subtitle
import taminx.core.core_ui.unit_rial
import taminx.core.core_ui.workers_payment_debt_label
import taminx.core.core_ui.workers_payment_month_count
import taminx.core.core_ui.workers_payment_subtitle
import taminx.core.core_ui.workers_payment_title

private val HEADER_OVERLAP = 24.dp

/** How much the debt headline shrinks once the summary card is fully collapsed. */
private const val AMOUNT_COLLAPSED_SCALE = 0.72f

/**
 * Floating hero header for the construction-worker premium screen — same shape as `PayRollHeader`
 * (gradient [TaminTopAppBar] + a card that rides up into it and morphs as the list scrolls),
 * without the pensioner chip / month picker that screen carries.
 */
@Composable
fun WorkersPaymentHeader(
    state: WorkersPaymentUiState,
    onBack: () -> Unit,
    collapseProgress: () -> Float = { 0f },
    onInfoClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(Res.string.workers_payment_title),
            background = gradient,
            bottomPadding = HEADER_OVERLAP,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.action_back),
                    onClick = onBack,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_info),
                    contentDescription = stringResource(Res.string.edict_search_title),
                    onClick = onInfoClicked,
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.page, vertical = Spacing.smPlus),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp
                    )
                    AnimatedRingHeaderIcon(icon = Icons.Outlined.Assignment)
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = stringResource(Res.string.workers_payment_subtitle),
                        style = MaterialTheme.typography.labelLarge,
                        color = taminColors.textHeaderSubtitle,
                    )
                }
            }
        }

        WorkersPaymentSummaryCard(
            state = state,
            collapseProgress = collapseProgress,
            modifier = Modifier
                .fillMaxWidth()
                .rideUpIntoHeader(
                    progress = collapseProgress,
                    expandedOverlap = HEADER_OVERLAP,
                    collapsedOverlap = HEADER_OVERLAP,
                )
                .padding(horizontal = Spacing.lg),
        )
    }
}

private enum class SummarySlot { Label, Amount, Chip }

private fun List<Measurable>.slot(id: SummarySlot): Measurable = first { it.layoutId == id }

/**
 * "بدهی قابل پرداخت" + total owed + a "N ماه" chip. [collapseProgress] drives the same
 * shrinking-[Layout] morph `PayRollMainCard` uses: expanded is a two-row card (label/chip, then the
 * big amount); collapsed is a single compact bar `[label  amount … chip]` that is genuinely shorter,
 * so the pinned header keeps clearing the list scrolling under it.
 */
@Composable
private fun WorkersPaymentSummaryCard(
    state: WorkersPaymentUiState,
    collapseProgress: () -> Float,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val rial = stringResource(Res.string.unit_rial)
    val monthCount = state.payableItems.size
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
    ) {
        Layout(
            content = {
                TaminText(
                    text = stringResource(Res.string.workers_payment_debt_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                    modifier = Modifier.layoutId(SummarySlot.Label),
                )
                Row(
                    modifier = Modifier
                        .layoutId(SummarySlot.Amount)
                        .shrinkOnCollapse(
                            progress = collapseProgress,
                            minScale = AMOUNT_COLLAPSED_SCALE,
                            rtl = rtl,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TaminText(
                        text = state.payableTotal.toPriceFormat(),
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
                CustomChip(
                    text = stringResource(
                        Res.string.workers_payment_month_count,
                        monthCount.toString()
                    ),
                    modifier = Modifier.layoutId(SummarySlot.Chip),
                    containerColor = taminColors.chipBg,
                    textColor = taminColors.blueText,
                    border = BorderStroke(1.dp, taminColors.blueBg),
                )
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val pad = Spacing.lg.roundToPx()
            val sm = Spacing.sm.roundToPx()
            val xs = Spacing.xs.roundToPx()
            val innerC = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))

            val label = measurables.slot(SummarySlot.Label).measure(innerC)
            val chip = measurables.slot(SummarySlot.Chip).measure(innerC)
            val amount = measurables.slot(SummarySlot.Amount).measure(innerC)

            val row1Height = maxOf(label.height, chip.height)
            val row1Top = pad
            val amountTop = row1Top + row1Height + sm
            val expandedH = amountTop + amount.height + pad

            val barRowHeight = maxOf(label.height, amount.height, chip.height)
            val collapsedH = pad + barRowHeight + pad

            val labelColY = pad + (barRowHeight - label.height) / 2
            val amountColY = pad + (barRowHeight - amount.height) / 2
            val chipColY = pad + (barRowHeight - chip.height) / 2

            val t = Easing.standard.transform(collapseProgress())

            layout(width, lerp(expandedH, collapsedH, t)) {
                label.placeRelative(
                    pad,
                    lerp(row1Top + (row1Height - label.height) / 2, labelColY, t),
                )
                chip.placeRelative(
                    width - pad - chip.width,
                    lerp(row1Top + (row1Height - chip.height) / 2, chipColY, t),
                )
                amount.placeRelative(
                    lerp(pad, pad + label.width + xs, t),
                    lerp(amountTop, amountColY, t),
                )
            }
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

private val PreviewItems = listOf(
    WorkersPaymentInfoPR(
        pay = false,
        payable = true,
        month = "07",
        monthTitle = "حق بیمه مهر",
        year = "1405",
        professionalTitle = "استاد لوله‌کش و نصاب وسایل بهداشتی",
        professional = "041597",
        rate = "1.9",
        days = "30",
        fromDatePersian = "14050701",
        toDatePersian = "14050730",
        amount = 22111981,
        amountFines = 0,
        totalPayable = 22111981,
        salary = 10529515,
        payDay = 5541850,
        payableDes = "هست",
        paymentDate = null,
        fishStatus = "دارد",
        maharatStatus = "دارد",
        bazresiStatus = "دارد",
        kargarStatus = "فعال می‌باشد.",
        type = "Premium",
        fromDateToDate = "1405070114050730",
    ),
    WorkersPaymentInfoPR(
        pay = false,
        payable = true,
        month = "08",
        monthTitle = "حق بیمه آبان",
        year = "1405",
        professionalTitle = "استاد لوله‌کش و نصاب وسایل بهداشتی",
        professional = "041597",
        rate = "1.9",
        days = "30",
        fromDatePersian = "14050801",
        toDatePersian = "14050830",
        amount = 22111981,
        amountFines = 1341000,
        totalPayable = 23452981,
        salary = 10529515,
        payDay = 5541850,
        payableDes = "هست",
        paymentDate = null,
        fishStatus = "دارد",
        maharatStatus = "دارد",
        bazresiStatus = "دارد",
        kargarStatus = "فعال می‌باشد.",
        type = "Premium",
        fromDateToDate = "1405080114050830",
    ),
)

private val PreviewState = WorkersPaymentUiState(
    items = PreviewItems.toImmutableList(),
    totalAmount = 45564962,
)

@PreviewRtlTheme
@Composable
private fun WorkersPaymentHeaderExpandedPreview() {
    PreviewRtlThemeContent {
        WorkersPaymentHeader(state = PreviewState, onBack = {}, onInfoClicked = {})
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentHeaderCollapsedPreview() {
    PreviewRtlThemeContent {
        WorkersPaymentHeader(
            state = PreviewState,
            onBack = {},
            collapseProgress = { 1f },
            onInfoClicked = {})
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentHeaderDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        WorkersPaymentHeader(state = PreviewState, onBack = {}, onInfoClicked = {})
    }
}
