package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
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
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workers_payment_empty_subtitle
import taminx.core.core_ui.workers_payment_empty_title

@Composable
fun WorkersPaymentRoute(
    viewModel: WorkersPaymentViewModel,
    onBackClicked: () -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    WorkersPaymentEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onOpenUrl = onOpenUrl,
        onNavigateBack = onBackClicked,
    )

    // No dedicated deep-link route for the gateway callback — instead, whenever the app comes back
    // to the foreground with a ticket still pending (i.e. we just returned from the bank), verify it.
    // Success populates uiState.paymentReceipt and screen 2 shows WorkersPaymentSuccessDialog.
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    LaunchedEffect(lifecycleState) {
        println("WorkersPaymentCallback: lifecycle=$lifecycleState hasPendingPayment=${viewModel.uiState.value.hasPendingPayment}") // TEMP
        if (lifecycleState == Lifecycle.State.RESUMED && viewModel.uiState.value.hasPendingPayment) {
            viewModel.sendIntent(WorkersPaymentIntent.VerifyPendingPayment)
        }
    }

    val onCloseDetail = { viewModel.sendIntent(WorkersPaymentIntent.ClosePaymentScreen) }

    if (uiState.selectedPaymentItem != null) {
        BackHandler(onBack = onCloseDetail)
    }

    // Full-screen slide between the list and the single-item confirmation, mirroring the
    // inspection request flow's AnimatedContent (RTL-aware Start/End directions).
    AnimatedContent(
        targetState = uiState.selectedPaymentItem,
        transitionSpec = {
            val direction = if (targetState != null) {
                AnimatedContentTransitionScope.SlideDirection.Start
            } else {
                AnimatedContentTransitionScope.SlideDirection.End
            }
            slideIntoContainer(direction, animationSpec = tween(300)) togetherWith
                slideOutOfContainer(direction, animationSpec = tween(300))
        },
        label = "WorkersPaymentScreenTransition",
    ) { selected ->
        if (selected != null) {
            WorkersPaymentDetailScreen(
                item = selected,
                isProcessing = uiState.isProcessingPayment,
                onIntent = viewModel::sendIntent,
                onBack = onCloseDetail,
                receipt = uiState.paymentReceipt,
                onDismissReceipt = { viewModel.sendIntent(WorkersPaymentIntent.DismissReceipt) },
            )
        } else {
            WorkersPaymentListScreen(
                uiState = uiState,
                onIntent = viewModel::sendIntent,
                onBack = onBackClicked,
            )
        }
    }
}

@Composable
private fun WorkersPaymentEvents(
    events: Flow<WorkersPaymentEvent>,
    onShowToast: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is WorkersPaymentEvent.ShowToast -> onShowToast(event.message)
            is WorkersPaymentEvent.OpenPaymentUrl -> onOpenUrl(event.url)
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
    var showInfoSheet by remember { mutableStateOf(false) }

    val topArea = rememberMeasuredTopAreaState { state ->
        WorkersPaymentHeader(
            state = uiState,
            onBack = onBack,
            topAreaState = state,
            onInfoClicked = { showInfoSheet = true },
        )
    }
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .driveTopArea(topArea, listState),
            contentPadding = topAreaContentPadding(
                state = topArea,
                rest = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg,
                ),
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {

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
            topAreaState = topArea,
            onInfoClicked = { showInfoSheet = true },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )

        if (uiState.isLoading && uiState.items.isNotEmpty()) {
            LoadingStateOverlay()
        }
    }

    if (showInfoSheet) {
        WorkersPaymentInfoBottomSheet(onDismiss = { showInfoSheet = false })
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
