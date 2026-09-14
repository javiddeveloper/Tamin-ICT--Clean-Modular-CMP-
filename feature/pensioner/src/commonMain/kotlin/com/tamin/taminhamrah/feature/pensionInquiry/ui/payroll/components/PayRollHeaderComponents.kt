package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollUiState
import com.tamin.taminhamrah.model.pension.PaymentTypeDN
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardShadow
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.jalali_months
import taminx.core.core_ui.payroll_payment_type_arrears
import taminx.core.core_ui.payroll_payment_type_bonus
import taminx.core.core_ui.payroll_payment_type_monthly
import taminx.core.core_ui.payroll_pensioner_chip
import taminx.core.core_ui.payroll_search_title
import taminx.core.core_ui.payroll_title

private val HEADER_OVERLAP = 24.dp

/** Floating header (Edict pattern): TaminTopAppBar + PayRollMainCard/PayRollEmptyCard. */
@Composable
fun PayRollHeader(
    state: PayRollUiState,
    onBack: () -> Unit,
    onIntent: (PayRollIntent) -> Unit,
    collapseProgress: () -> Float = { 0f },
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val hasData = state.payRollList.isNotEmpty()

    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(Res.string.payroll_title),
            background = profileGradientBrush,
            bottomPadding = HEADER_OVERLAP,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.action_back),
                    onClick = onBack,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = stringResource(Res.string.payroll_search_title),
                    onClick = { onIntent(PayRollIntent.ShowSearchSheet) },
                )
            },
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                DecorativeBackgroundCircle(
                    size = 190.dp,
                    xOffset = 450.dp,
                    yOffset = (-150).dp,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.md, bottom = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    PayRollPensionerChip(
                        state = state,
                        onIntent = onIntent,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    if (state.isDateFilteredBySearch && state.startDate.isNotEmpty()) {
                        PayRollFilterChip(
                            startDate = state.startDate,
                            paymentType = state.paymentType,
                            onClear = { onIntent(PayRollIntent.ClearDateFilter) },
                        )
                    } else {
                        PayRollDateChipsRow(state = state, onIntent = onIntent)
                    }
                }
            }
        }

        val cardModifier = Modifier
            .fillMaxWidth()
            .rideUpIntoHeader(
                progress = collapseProgress,
                expandedOverlap = HEADER_OVERLAP,
                collapsedOverlap = HEADER_OVERLAP,
            )
            .padding(horizontal = Spacing.lg)

        // Loading is checked first, and on every load rather than only the first: a refetch
        // after a date or pensioner change replaces the card with its skeleton, where it used
        // to leave the previous payslip on screen under a scrim -- numbers from the old query
        // that read as the answer to the new one.
        if (state.isLoading) {
            PayRollSkeletonMainCard(modifier = cardModifier)
        } else if (hasData) {
            PayRollMainCard(
                state = state,
                collapseProgress = collapseProgress,
                modifier = cardModifier.shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(CornerRadius.card),
                    ambientColor = TaminIdentityCardShadow,
                    spotColor = TaminIdentityCardShadow,
                ),
            )
        } else if (state.hasLoadedOnce) {
            PayRollEmptyCard(
                onShowAll = { onIntent(PayRollIntent.ClearDateFilter) },
                modifier = cardModifier,
            )
        }
    }
}

@Composable
private fun PayRollPensionerChip(
    state: PayRollUiState,
    onIntent: (PayRollIntent) -> Unit,
    modifier: Modifier,
) {
    val taminColors = LocalTaminColors.current
    val isLoading = state.selectedPensionerId == null
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(Color.White.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = taminColors.border,
                shape = RoundedCornerShape(CornerRadius.card),
            )
            .clickable(enabled = !isLoading) { onIntent(PayRollIntent.ShowPensionerSheet) }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(CornerRadius.sm))
                        .shimmer(
                            colorBase = Color.White.copy(alpha = 0.15f),
                            colorHighlight = Color.White.copy(alpha = 0.45f),
                        ),
                )
            } else {
                TaminText(
                    text = stringResource(Res.string.payroll_pensioner_chip, state.selectedPensionerId.orEmpty()),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.width(Spacing.sm))
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun PayRollFilterChip(
    startDate: String,
    paymentType: String,
    onClear: () -> Unit,
) {
    val label = "${formatPayRollDateLabel(startDate)} · ${payRollPaymentTypeLabel(paymentType)}"
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.chip))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onClear),
            )
            Spacer(Modifier.width(Spacing.xs))
            TaminText(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun PayRollDateChipsRow(
    state: PayRollUiState,
    onIntent: (PayRollIntent) -> Unit,
) {
    val dates = remember { lastFivePayRollMonths() }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(dates) { date ->
            val isSelected = date == state.startDate && state.paymentType == PaymentTypeDN.MONTHLY.code
            val bgColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.12f)
            val textColor = if (isSelected) Color.Black else Color.White

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(bgColor)
                    .clickable {
                        onIntent(PayRollIntent.ChangeStartDate(date))
                        onIntent(PayRollIntent.ChangePaymentType(PaymentTypeDN.MONTHLY.code))
                        onIntent(PayRollIntent.LoadPayRoll)
                    }
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TaminText(
                    text = formatPayRollDateLabel(date),
                    color = textColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                TaminText(
                    text = stringResource(Res.string.payroll_payment_type_monthly),
                    color = textColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

/**
 * Last 5 calendar months *before* the current one (most recent first), e.g. تیر ۱۴۰۵ → اسفند
 * ۱۴۰۴ when the current month is مرداد — the current month's payroll isn't issued yet, so it's
 * excluded, rolling across years.
 */
private fun lastFivePayRollMonths(): List<String> {
    val (currentYear, currentMonth, _) = PersianDateFormatter.today()
    return (1..5).map { offset ->
        var year = currentYear
        var month = currentMonth - offset
        while (month <= 0) {
            month += 12
            year -= 1
        }
        "$year${month.toString().padStart(2, '0')}"
    }
}

@Composable
internal fun formatPayRollDateLabel(date: String): String {
    if (date.length < 5) return ""
    val year = date.substring(0, 4)
    val monthStr = date.substring(4)
    val monthIndex = monthStr.toIntOrNull()?.minus(1) ?: return date
    val months = stringArrayResource(Res.array.jalali_months)
    return if (monthIndex in months.indices) "${months[monthIndex]} ${year.toPersianDigits()}" else date.toPersianDigits()
}

@Composable
internal fun payRollPaymentTypeLabel(code: String): String = when (PaymentTypeDN.fromCode(code)) {
    PaymentTypeDN.MONTHLY -> stringResource(Res.string.payroll_payment_type_monthly)
    PaymentTypeDN.BONUS -> stringResource(Res.string.payroll_payment_type_bonus)
    PaymentTypeDN.ARREARS_INCREASE -> stringResource(Res.string.payroll_payment_type_arrears)
}

// ─── Preview data ─────────────────────────────────────────────────────────────

private val PreviewHeaderItems = listOf(
    PayRollPR(id = 1, clpType = "1", tprDesc = "مبلغ مستمری", sumAmount = 41008708, sumPay = 40852660, hisYear = "1405", hisMon = "05"),
    PayRollPR(id = 2, clpType = "1", tprDesc = "کمک هزینه عائله‌مندی", sumAmount = 439980),
    PayRollPR(id = 3, clpType = "1", tprDesc = "کمک به تأمین معیشت", sumAmount = 385000),
    PayRollPR(id = 4, clpType = "2", tprDesc = "بیمه عمر", sumAmount = -71600),
    PayRollPR(id = 5, clpType = "2", tprDesc = "بیمه درمان تکمیلی", sumAmount = -584000),
)

private val PreviewHeaderState = PayRollUiState(
    isLoading = false,
    hasLoadedOnce = true,
    selectedPensionerId = "1003406938",
    startDate = "140505",
    paymentType = PaymentTypeDN.MONTHLY.code,
    pensionerIds = listOf(PensionIdPR("1003406938"), PensionIdPR("2003406939")),
    payRollList = PreviewHeaderItems,
)

// ─── Previews ─────────────────────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun PayRollHeaderPreview() {
    PreviewRtlThemeContent {
        PayRollHeader(
            state = PreviewHeaderState,
            onBack = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollHeaderCollapsedPreview() {
    PreviewRtlThemeContent {
        PayRollHeader(
            state = PreviewHeaderState,
            onBack = {},
            onIntent = {},
            collapseProgress = { 1f },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollHeaderLoadingPreview() {
    PreviewRtlThemeContent {
        PayRollHeader(
            state = PayRollUiState(isLoading = true, hasLoadedOnce = false),
            onBack = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollHeaderEmptyPreview() {
    PreviewRtlThemeContent {
        PayRollHeader(
            state = PreviewHeaderState.copy(payRollList = emptyList()),
            onBack = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollHeaderFilteredPreview() {
    PreviewRtlThemeContent {
        PayRollHeader(
            state = PreviewHeaderState.copy(
                isDateFilteredBySearch = true,
                startDate = "140503",
                paymentType = PaymentTypeDN.BONUS.code,
            ),
            onBack = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollPensionerChipPreview() {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier
                .background(LocalTaminColors.current.blueText)
                .padding(Spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                PayRollPensionerChip(state = PreviewHeaderState, onIntent = {}, modifier = Modifier)
                PayRollPensionerChip(
                    state = PayRollUiState(selectedPensionerId = null),
                    onIntent = {},
                    modifier = Modifier,
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollFilterChipPreview() {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier
                .background(LocalTaminColors.current.blueText)
                .padding(Spacing.lg),
        ) {
            PayRollFilterChip(
                startDate = "140503",
                paymentType = PaymentTypeDN.BONUS.code,
                onClear = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollDateChipsRowPreview() {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier
                .background(LocalTaminColors.current.blueText)
                .padding(Spacing.lg),
        ) {
            PayRollDateChipsRow(state = PreviewHeaderState, onIntent = {})
        }
    }
}
