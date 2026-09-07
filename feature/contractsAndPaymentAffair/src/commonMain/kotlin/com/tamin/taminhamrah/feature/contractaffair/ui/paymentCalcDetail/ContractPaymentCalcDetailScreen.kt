package com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.components.ContractPaymentCalcDetailHeader
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.components.PaymentCalcDetailRow
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.contract.ContractPaymentCalcDetailEvent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.contract.ContractPaymentCalcDetailIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.contract.ContractPaymentCalcDetailUiState
import com.tamin.taminhamrah.model.contractAffair.PaymentCalcLinePR
import com.tamin.taminhamrah.model.contractAffair.PaymentCalcMonthPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_payment_details_empty_subtitle
import taminx.core.core_ui.contract_payment_details_empty_title
import taminx.core.core_ui.contract_payment_details_period
import taminx.core.core_ui.contract_payment_details_total

@Composable
fun ContractPaymentCalcDetailRoute(
    viewModel: ContractPaymentCalcDetailViewModel,
    premiumTypeCode: String,
    startDate: Long,
    endDate: Long,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(premiumTypeCode, startDate, endDate) {
        viewModel.sendIntent(
            ContractPaymentCalcDetailIntent.Load(premiumTypeCode, startDate, endDate),
        )
    }

    ContractPaymentCalcDetailEvents(events = viewModel.events, onBackClicked = onBackClicked)

    ContractPaymentCalcDetailScreen(uiState = uiState, onIntent = viewModel::sendIntent)
}

@Composable
private fun ContractPaymentCalcDetailEvents(
    events: Flow<ContractPaymentCalcDetailEvent>,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ContractPaymentCalcDetailEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
internal fun ContractPaymentCalcDetailScreen(
    uiState: ContractPaymentCalcDetailUiState,
    onIntent: (ContractPaymentCalcDetailIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ContractPaymentCalcDetailHeader(
                monthCountLabel = uiState.monthCountLabel,
                onBackClicked = { onIntent(ContractPaymentCalcDetailIntent.OnBackClicked) },
            )

            when {
                uiState.isLoading && uiState.rows.isEmpty() -> LoadingStateOverlay()

                uiState.rows.isEmpty() -> EmptyStateMessage(
                    icon = Icons.Outlined.ReceiptLong,
                    title = stringResource(Res.string.contract_payment_details_empty_title),
                    subtitle = stringResource(Res.string.contract_payment_details_empty_subtitle),
                    showIconTile = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Spacing.xlg),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    overscrollEffect = rememberJellyOverscroll(),
                    contentPadding = PaddingValues(
                        top = Spacing.lg,
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + Spacing.xxl,
                        start = Spacing.page,
                        end = Spacing.page,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    item {
                        SummaryCard(
                            startLabel = uiState.startLabel,
                            endLabel = uiState.endLabel,
                            totalLabel = uiState.totalLabel,
                        )
                    }
                    items(uiState.rows) { month ->
                        PaymentCalcDetailRow(month = month)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    startLabel: String,
    endLabel: String,
    totalLabel: String,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.blueBg, RoundedCornerShape(CornerRadius.lg))
            .border(
                width = 1.dp,
                color = colors.blueBorder,
                shape = RoundedCornerShape(CornerRadius.lg)
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(colors.bgSurface, RoundedCornerShape(CornerRadius.xl)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            TaminText(
                text = stringResource(Res.string.contract_payment_details_period),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                NumericText(
                    text = startLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                    color = colors.textPrimary,
                )
                TaminText(
                    text = "—",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                    color = colors.textMuted,
                )
                NumericText(
                    text = endLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                    color = colors.textPrimary,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            TaminText(
                text = stringResource(Res.string.contract_payment_details_total),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            TaminText(
                text = totalLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.greenText,
            )
        }
    }
}

// ---- previews ----

private val PreviewRows = persistentListOf(
    PaymentCalcMonthPR(
        monthTitle = "شهریور ۱۴۰۵",
        monthNumberLabel = "۶",
        daysLabel = "۲۳",
        baseWageRaw = 152_955_060L,
        ratePercent = 30,
        lines = persistentListOf(
            PaymentCalcLinePR(label = "حق بیمه", amountRaw = 45_886_518L, isDeduction = false),
            PaymentCalcLinePR(label = "کمک دولت", amountRaw = -4_588_652L, isDeduction = true),
        ),
        netAmountRaw = 41_297_866L,
    ),
    PaymentCalcMonthPR(
        monthTitle = "مهر ۱۴۰۵",
        monthNumberLabel = "۷",
        daysLabel = "۳۰",
        baseWageRaw = 199_506_600L,
        ratePercent = 27,
        lines = persistentListOf(
            PaymentCalcLinePR(label = "حق بیمه", amountRaw = 53_866_782L, isDeduction = false),
        ),
        netAmountRaw = 53_866_782L,
    ),
).toImmutableList()

@PreviewRtlTheme
@Composable
private fun ContractPaymentCalcDetailScreenPreviewLight() {
    PreviewRtlThemeContent {
        ContractPaymentCalcDetailScreen(
            uiState = ContractPaymentCalcDetailUiState(
                startLabel = "۱۴۰۵/۰۷/۰۱",
                endLabel = "۱۴۰۵/۰۸/۳۰",
                rows = PreviewRows,
                monthCountLabel = "۲",
                totalLabel = "۱۰۷٬۷۳۳٬۵۶۴ ریال",
            ),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentCalcDetailScreenPreviewEmptyDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractPaymentCalcDetailScreen(
            uiState = ContractPaymentCalcDetailUiState(monthCountLabel = "۲"),
            onIntent = {},
        )
    }
}
