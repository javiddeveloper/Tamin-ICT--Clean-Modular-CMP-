package com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.components.ContractPaymentHistoryHeader
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.components.ContractPaymentHistorySkeleton
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.components.PaymentHistoryItemCard
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.components.PaymentHistorySummaryCard
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.contract.ContractPaymentHistoryEvent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.contract.ContractPaymentHistoryIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.contract.ContractPaymentHistoryUiState
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_payment_history_empty_subtitle
import taminx.core.core_ui.contract_payment_history_empty_title


@Composable
fun ContractPaymentHistoryRoute(
    viewModel: ContractPaymentHistoryViewModel,
    contractNumber: String,
    insuranceType: String,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(contractNumber, insuranceType) {
        viewModel.sendIntent(
            ContractPaymentHistoryIntent.Load(contractNumber, insuranceType),
        )
    }

    ContractPaymentHistoryEvents(events = viewModel.events, onBackClicked = onBackClicked)

    ContractPaymentHistoryScreen(uiState = uiState, onIntent = viewModel::sendIntent)
}

@Composable
private fun ContractPaymentHistoryEvents(
    events: Flow<ContractPaymentHistoryEvent>,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ContractPaymentHistoryEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
internal fun ContractPaymentHistoryScreen(
    uiState: ContractPaymentHistoryUiState,
    onIntent: (ContractPaymentHistoryIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ContractPaymentHistoryHeader(
                insuranceType = uiState.insuranceType,
                contractNumber = uiState.contractNumber,
                onBackClicked = { onIntent(ContractPaymentHistoryIntent.OnBackClicked) },
            )

            when {
                uiState.isLoading && uiState.items.isEmpty() -> ContractPaymentHistorySkeleton()

                uiState.items.isEmpty() -> EmptyStateMessage(
                    icon = Icons.Outlined.ReceiptLong,
                    title = stringResource(Res.string.contract_payment_history_empty_title),
                    subtitle = stringResource(Res.string.contract_payment_history_empty_subtitle),
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
                        PaymentHistorySummaryCard(
                            successfulTotalLabel = uiState.successfulTotalLabel,
                            paymentCount = uiState.paymentCount,
                        )
                    }
                    items(uiState.items, key = { it.debtNumber }) { item ->
                        PaymentHistoryItemCard(item = item)
                    }
                }
            }
        }
    }
}

// ---- previews ----

private val PreviewItems = persistentListOf(
    ContractPaymentHistoryItemPR(
        debtNumber = "۹۰۵۰۱۱۳۶۱۳۹۶",
        amountPayment = "55662341",
        datePayment = "۱۴۰۵/۰۴/۱۶",
        totalDebt = "55662341",
        paymentDeadline = "۱۴۰۵/۰۵/۱۵",
        termStart = "۱۴۰۵/۰۲/۰۱",
        termEnd = "۱۴۰۵/۰۴/۳۱",
        collectionStatus = "وصول شده",
        isPaid = true,
        statusLabel = "پرداخت شده",
    ),
    ContractPaymentHistoryItemPR(
        debtNumber = "۹۰۵۰۱۱۶۵۷۹۲۹",
        amountPayment = "0",
        datePayment = "",
        totalDebt = "58940120",
        paymentDeadline = "",
        termStart = "۱۴۰۵/۰۵/۰۱",
        termEnd = "۱۴۰۵/۰۷/۳۱",
        collectionStatus = "در انتظار",
        isPaid = false,
        statusLabel = "پرداخت نشده",
    ),
).toImmutableList()

@PreviewRtlTheme
@Composable
private fun ContractPaymentHistoryScreenPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractPaymentHistoryScreen(
                uiState = ContractPaymentHistoryUiState(
                    contractNumber = "۴۸۳۲۲۲۲۶۸۶",
                    insuranceType = "بیمهٔ اختیاری",
                    items = PreviewItems,
                    successfulTotalLabel = "۱۰۳٬۹۷۳٬۲۴۱ ریال",
                ),
                onIntent = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentHistoryScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            ContractPaymentHistoryScreen(
                uiState = ContractPaymentHistoryUiState(
                    contractNumber = "۴۸۳۲۲۲۲۶۸۶",
                    insuranceType = "بیمهٔ اختیاری",
                    items = PreviewItems,
                    successfulTotalLabel = "۱۰۳٬۹۷۳٬۲۴۱ ریال",
                ),
                onIntent = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentHistoryScreenPreviewEmpty() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            ContractPaymentHistoryScreen(
                uiState = ContractPaymentHistoryUiState(
                    contractNumber = "۴۸۳۲۲۲۲۶۸۶",
                    insuranceType = "بیمهٔ اختیاری",
                ),
                onIntent = {},
            )
        }
    }
}
