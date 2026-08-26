package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.workersPayment.WorkersPaymentViewModel
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentEvent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentIntent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentUiState
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentHeader
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentInfoBottomSheet
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentListSkeleton
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentMonthCard
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workers_payment_empty_subtitle
import taminx.core.core_ui.workers_payment_empty_title

private val HeaderCollapseDistance = 140.dp

@Composable
fun WorkersPaymentRoute(
    viewModel: WorkersPaymentViewModel,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    WorkersPaymentEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBackClicked,
    )

    if (uiState.selectedPaymentItem != null) {
        // TODO(screen 2): the payment/confirmation screen is provided separately by product.
        // It is driven by this same ViewModel — open it via WorkersPaymentIntent.OpenPaymentScreen,
        // leave it via WorkersPaymentIntent.ClosePaymentScreen, and reuse PayItem/VerifyPendingPayment.
        BackHandler(onBack = { viewModel.sendIntent(WorkersPaymentIntent.ClosePaymentScreen) })
        WorkersPaymentScreenTwoPlaceholder(
            item = uiState.selectedPaymentItem!!,
            onBack = { viewModel.sendIntent(WorkersPaymentIntent.ClosePaymentScreen) },
        )
    } else {
        WorkersPaymentListScreen(
            uiState = uiState,
            onIntent = viewModel::sendIntent,
            onBack = onBackClicked,
        )
    }
}

@Composable
private fun WorkersPaymentEvents(
    events: Flow<WorkersPaymentEvent>,
    onShowToast: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is WorkersPaymentEvent.ShowToast -> onShowToast(event.message)
            is WorkersPaymentEvent.PaymentVerified -> onShowToast(event.message)
            is WorkersPaymentEvent.OpenPaymentUrl -> Unit // handled by screen 2
            WorkersPaymentEvent.NavigateBack -> onNavigateBack()
        }
    }
}

@Composable
internal fun WorkersPaymentListScreen(
    uiState: WorkersPaymentUiState,
    onIntent: (WorkersPaymentIntent) -> Unit,
    onBack: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val collapse = rememberCollapsingHeaderState(HeaderCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var showInfoSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item { Spacer(modifier = Modifier.reservedHeight { headerHeightPx }) }

            when {
                uiState.isLoading && uiState.items.isEmpty() -> {
                    item { WorkersPaymentListSkeleton() }
                }

                uiState.items.isEmpty() -> {
                    item {
                        EmptyStateMessage(
                            icon = Icons.Outlined.Verified,
                            title = stringResource(Res.string.workers_payment_empty_title),
                            subtitle = stringResource(Res.string.workers_payment_empty_subtitle),
                            showIconTile = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .padding(horizontal = Spacing.xlg),
                        )
                    }
                }

                else -> {
                    items(uiState.items, key = { it.fromDateToDate }) { item ->
                        WorkersPaymentMonthCard(
                            item = item,
                            onPayClicked = { onIntent(WorkersPaymentIntent.OpenPaymentScreen(item)) },
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }
                }
            }
        }

        WorkersPaymentHeader(
            state = uiState,
            onBack = onBack,
            collapseProgress = collapse.progressProvider,
            onInfoClicked = { showInfoSheet = true },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        )

        if (uiState.isLoading && uiState.items.isNotEmpty()) {
            LoadingStateOverlay()
        }
    }

    if (showInfoSheet) {
        WorkersPaymentInfoBottomSheet(onDismiss = { showInfoSheet = false })
    }
}

@Composable
private fun WorkersPaymentScreenTwoPlaceholder(
    item: WorkersPaymentInfoPR,
    onBack: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
        contentAlignment = Alignment.Center,
    ) {
        TaminText(
            text = "صفحهٔ پرداخت «${item.monthTitle}» — به‌زودی",
            color = taminColors.textSecondary,
            modifier = Modifier.padding(Spacing.xlg),
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

private val PreviewItems = listOf(
    WorkersPaymentInfoPR(
        pay = false, payable = true, month = "07", monthTitle = "حق بیمه مهر", year = "1405",
        professionalTitle = "استاد لوله‌کش و نصاب وسایل بهداشتی", professional = "041597", rate = "1.9",
        days = "30", fromDatePersian = "14050701", toDatePersian = "14050730",
        amount = 22111981, amountFines = 0, totalPayable = 22111981, salary = 10529515, payDay = 5541850,
        payableDes = "هست", paymentDate = null, fishStatus = "دارد", maharatStatus = "دارد",
        bazresiStatus = "دارد", kargarStatus = "فعال می‌باشد.", type = "Premium",
        fromDateToDate = "1405070114050730",
    ),
    WorkersPaymentInfoPR(
        pay = false, payable = true, month = "04", monthTitle = "حق بیمه تیر", year = "1405",
        professionalTitle = "بنّای سفت‌کار", professional = "7112", rate = "1.9",
        days = "31", fromDatePersian = "14050401", toDatePersian = "14050431",
        amount = 10281000, amountFines = 1341000, totalPayable = 11622000, salary = 2388000, payDay = 5541850,
        payableDes = "هست", paymentDate = null, fishStatus = "دارد", maharatStatus = "دارد",
        bazresiStatus = "دارد", kargarStatus = "فعال می‌باشد.", type = "Premium",
        fromDateToDate = "1405040114050431",
    ),
    WorkersPaymentInfoPR(
        pay = true, payable = false, month = "03", monthTitle = "حق بیمه خرداد", year = "1404",
        professionalTitle = "بنّای سفت‌کار", professional = "7112", rate = "1.9",
        days = "30", fromDatePersian = "14040301", toDatePersian = "14040330",
        amount = 8652000, amountFines = 0, totalPayable = 8652000, salary = 2312000, payDay = 5541850,
        payableDes = "هست", paymentDate = "14040412", fishStatus = "دارد", maharatStatus = "دارد",
        bazresiStatus = "دارد", kargarStatus = "فعال می‌باشد.", type = "Premium",
        fromDateToDate = "1404030114040330",
    ),
)

private val PreviewState = WorkersPaymentUiState(items = PreviewItems.toImmutableList())

@PreviewRtlTheme
@Composable
private fun WorkersPaymentListScreenPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkersPaymentListScreen(uiState = PreviewState, onIntent = {}, onBack = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentListScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            WorkersPaymentListScreen(uiState = PreviewState, onIntent = {}, onBack = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentListScreenLoadingPreview() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkersPaymentListScreen(
                uiState = WorkersPaymentUiState(isLoading = true, items = persistentListOf()),
                onIntent = {},
                onBack = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentListScreenEmptyPreview() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkersPaymentListScreen(
                uiState = WorkersPaymentUiState(isLoading = false, items = persistentListOf()),
                onIntent = {},
                onBack = {},
            )
        }
    }
}
