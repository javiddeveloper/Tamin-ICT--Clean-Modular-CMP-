package com.tamin.taminhamrah.feature.contractaffair.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractAffairsActionRow
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractAffairsHeader
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractAffairsItemCard
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractAffairsListSkeleton
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractCancelScreen
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractCancelSuccessDialog
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractOperationsSheet
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractSearchEmptyState
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractSearchFilterChipRow
import com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractSearchSheet
import com.tamin.taminhamrah.feature.contractaffair.ui.components.NewContractSheet
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractAffairsEvent
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractAffairsIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractAffairsUiState
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractOperation
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractSearchFilter
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.contractAffair.ContractPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
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
import taminx.core.core_ui.contract_affairs_empty_subtitle
import taminx.core.core_ui.contract_affairs_empty_title
import taminx.core.core_ui.contract_affairs_report_filename_format
import taminx.core.core_ui.contract_affairs_view_contract

/**
 * امور قراردادها و پرداخت.
 *
 * Route → Events → Screen, matching `InspectionScreen`. The per-contract امور قرارداد bottom sheet
 * ([ContractOperationsSheet]) is wired here, and «مشاهدهٔ پرداخت‌ها» navigates out to the سوابق
 * پرداخت screen via [ContractAffairsEvent.NavigateToPaymentHistory]. The غیرفعال کردن flow and PDF
 * viewer are separate sheets wired in a later step — their state already lives on
 * [ContractAffairsUiState].
 */
@Composable
fun ContractAffairsRoute(
    viewModel: ContractAffairsViewModel,
    onBackClicked: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onNavigateToPaymentHistory: (contractNumber: String, insuranceType: String) -> Unit,
    onNavigateToPremiumPayment: (
        contractNumber: String,
        premiumTypeCode: String,
        insuranceType: String,
    ) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContractAffairsEvents(
        events = viewModel.events,
        onNavigateToService = onNavigateToService,
        onNavigateToWeb = onOpenUrl,
        onNavigateToPaymentHistory = onNavigateToPaymentHistory,
        onNavigateToPremiumPayment = onNavigateToPremiumPayment,
    )

    ContractAffairsScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
private fun ContractAffairsEvents(
    events: Flow<ContractAffairsEvent>,
    onNavigateToService: (FeatureFlag) -> Unit,
    onNavigateToWeb: (String) -> Unit,
    onNavigateToPaymentHistory: (contractNumber: String, insuranceType: String) -> Unit,
    onNavigateToPremiumPayment: (
        contractNumber: String,
        premiumTypeCode: String,
        insuranceType: String,
    ) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ContractAffairsEvent.NavigateToService -> onNavigateToService(event.flag)
            is ContractAffairsEvent.NavigateToWeb -> onNavigateToWeb(event.url)
            is ContractAffairsEvent.NavigateToPaymentHistory ->
                onNavigateToPaymentHistory(event.contractNumber, event.insuranceType)

            is ContractAffairsEvent.NavigateToPremiumPayment -> onNavigateToPremiumPayment(
                event.contract.contractNumber,
                event.contract.premiumTypeCode,
                event.contract.insuranceType,
            )

            is ContractAffairsEvent.NavigateToEditContract -> Unit // TODO(ui): edit-contract flow
            is ContractAffairsEvent.ContractCancelled -> Unit // TODO(ui): success confirmation
            is ContractAffairsEvent.ShowToast -> Unit // TODO(ui): snackbar
            is ContractAffairsEvent.ShowError -> Unit // TODO(ui): error snackbar
        }
    }
}

@Composable
internal fun ContractAffairsScreen(
    uiState: ContractAffairsUiState,
    onIntent: (ContractAffairsIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    // Drag budget is measured from the real header at both extremes — same collapsing behaviour as
    // ActiveRelationScreen. Only read inside the header's layout/draw lambdas, so the fold never
    // recomposes the screen.
    val topArea = rememberMeasuredTopAreaState { state ->
        ContractAffairsHeader(onBackClicked = {}, onSearchClicked = {}, topAreaState = state)
    }
    val listState = rememberLazyListState()
    var showNewContractSheet by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }

    val staggerState = rememberStaggeredEntranceState(key = uiState.contracts.size)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            state = listState,
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .driveTopArea(topArea, listState),
            contentPadding = topAreaContentPadding(
                state = topArea,
                rest = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + Spacing.lg,
                ),
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                ContractAffairsActionRow(
                    modifier = Modifier.padding(top = Spacing.xs),
                    contractCount = uiState.contracts.size,
                    onNewContractClicked = { showNewContractSheet = true },
                )
            }

            when {
                uiState.isLoading && uiState.contracts.isEmpty() -> item {
                    ContractAffairsListSkeleton()
                }

                uiState.contracts.isEmpty() && uiState.isSearchActive -> item {
                    ContractSearchEmptyState(
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                    )
                }

                uiState.contracts.isEmpty() -> item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Description,
                        title = stringResource(Res.string.contract_affairs_empty_title),
                        subtitle = stringResource(Res.string.contract_affairs_empty_subtitle),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }

                else -> {
                    if (uiState.isSearchActive) {
                        item {
                            ContractSearchFilterChipRow(
                                contractNumber = uiState.searchContractNumber,
                                filter = uiState.searchFilter,
                                onClear = { onIntent(ContractAffairsIntent.ClearSearch) },
                                modifier = Modifier.padding(horizontal = Spacing.lg),
                            )
                        }
                    }

                    itemsIndexed(
                        uiState.contracts,
                        key = { _, contract -> contract.contractNumber },
                    ) { index, contract ->
                        ContractAffairsItemCard(
                            item = contract,
                            onOperationsClicked = {
                                onIntent(ContractAffairsIntent.ShowContractOperations(contract))
                            },
                            onPrimaryActionClicked = {
                                onIntent(
                                    ContractAffairsIntent.OnOperationClick(
                                        contract = contract,
                                        operation = primaryOperationFor(contract),
                                    ),
                                )
                            },
                            modifier = Modifier
                                .padding(horizontal = Spacing.lg)
                                .staggeredItemEntrance(
                                    index = index,
                                    key = contract.contractNumber,
                                    state = staggerState,
                                ),
                        )
                    }
                }
            }
        }

        ContractAffairsHeader(
            onBackClicked = onBackClicked,
            onSearchClicked = { showSearchSheet = true },
            topAreaState = topArea,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )
    }

    if (showNewContractSheet) {
        NewContractSheet(
            onServiceClick = { flag ->
                onIntent(ContractAffairsIntent.OnNewContractOptionClick(flag))
                showNewContractSheet = false
            },
            onDismiss = { showNewContractSheet = false },
        )
    }

    uiState.operationsContract?.let { operationsContract ->
        ContractOperationsSheet(
            contract = operationsContract,
            operations = uiState.operations,
            onOperationClick = { operation ->
                onIntent(
                    ContractAffairsIntent.OnOperationClick(
                        contract = operationsContract,
                        operation = operation,
                    ),
                )
            },
            onDismiss = { onIntent(ContractAffairsIntent.DismissContractOperations) },
        )
    }

    if (uiState.showCancelSheet) {
        uiState.cancelContract?.let { cancelContract ->
            ContractCancelScreen(
                contract = cancelContract,
                reasons = uiState.cancelReasons,
                isReasonsLoading = uiState.isCancelReasonsLoading,
                selectedReason = uiState.selectedCancelReason,
                description = uiState.cancelDescription,
                isSubmitting = uiState.isCancelling,
                onReasonSelected = { onIntent(ContractAffairsIntent.OnCancelReasonSelected(it)) },
                onDescriptionChanged = {
                    onIntent(ContractAffairsIntent.OnCancelDescriptionChanged(it))
                },
                onConfirm = { onIntent(ContractAffairsIntent.ConfirmCancelContract) },
                onBack = { onIntent(ContractAffairsIntent.DismissCancelSheet) },
            )
        }
    }

    if (uiState.showCancelSuccess) {
        ContractCancelSuccessDialog(
            onDismiss = { onIntent(ContractAffairsIntent.DismissCancelSuccess) },
        )
    }

    if (uiState.showPdfViewer) {
        uiState.pdfContract?.let { pdfContract ->
            TaminPdfViewer(
                fileName = stringResource(
                    Res.string.contract_affairs_report_filename_format,
                    pdfContract.contractNumber,
                ),
                pdf = uiState.pdfDownload,
                downloadFailed = uiState.pdfDownloadFailed,
                title = stringResource(Res.string.contract_affairs_view_contract),
                onRequestDownload = { onIntent(ContractAffairsIntent.RetryPdfDownload) },
                onDismiss = { onIntent(ContractAffairsIntent.DismissPdfViewer) },
            )
        }
    }

    if (showSearchSheet) {
        ContractSearchSheet(
            contractNumber = uiState.searchContractNumber,
            filter = uiState.searchFilter,
            onApply = { number, selectedFilter ->
                onIntent(ContractAffairsIntent.ApplySearch(number, selectedFilter))
                showSearchSheet = false
            },
            onClear = {
                onIntent(ContractAffairsIntent.ClearSearch)
                showSearchSheet = false
            },
            onDismiss = { showSearchSheet = false },
        )
    }
}

/** پرداخت حق بیمه for every type but تکمیل/کسری (fraction), whose only action is مشاهدهٔ قرارداد. */
private fun primaryOperationFor(contract: ContractPR): ContractOperation =
    if (contract.premiumTypeCode == "38") ContractOperation.VIEW_CONTRACT
    else ContractOperation.PAY_PREMIUM

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsScreen(
                uiState = ContractAffairsUiState(contracts = PreviewMockContracts),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            ContractAffairsScreen(
                uiState = ContractAffairsUiState(contracts = PreviewMockContracts),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenLoading() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsScreen(
                uiState = ContractAffairsUiState(isLoading = true, contracts = persistentListOf()),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenEmpty() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsScreen(
                uiState = ContractAffairsUiState(contracts = persistentListOf()),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenSearchNoResult() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsScreen(
                uiState = ContractAffairsUiState(
                    contracts = persistentListOf(),
                    isSearchActive = true,
                    searchContractNumber = "0000000000",
                    searchFilter = ContractSearchFilter.OPTIONAL,
                ),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenSearchResults() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            ContractAffairsScreen(
                uiState = ContractAffairsUiState(
                    contracts = PreviewMockContracts,
                    isSearchActive = true,
                    searchFilter = ContractSearchFilter.FREELANCE,
                ),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

private val PreviewMockContracts = listOf(
    ContractPR(
        contractNumber = "4832222686",
        statusDesc = "فعال بعلت تنظیم قرارداد",
        isActive = true,
        requestDate = "۱۴۰۵/۰۴/۰۱",
        insuranceType = "بیمهٔ اختیاری",
        monthlyPremiumLabel = "بیمه اختیاری ۲۷ درصد",
        monthlyIncome = "199506600",
        treatmentSupportText = "حمایت درمان دارد",
        hasTreatmentSupport = true,
        jobTitle = "",
        premiumTypeCode = "02",
        statusCode = 1,
        freeJobCode = "",
        premiumRatePercentLabel = "۲۷ درصد",
    ),
    ContractPR(
        contractNumber = "4811907432",
        statusDesc = "فعال",
        isActive = true,
        requestDate = "۱۴۰۴/۱۱/۱۲",
        insuranceType = "حرف و مشاغل آزاد",
        monthlyPremiumLabel = "حرف و مشاغل ۱۸ درصد",
        monthlyIncome = "104250000",
        treatmentSupportText = "حمایت درمان دارد",
        hasTreatmentSupport = true,
        jobTitle = "رانندهٔ تاکسی شهری",
        premiumTypeCode = "01",
        statusCode = 1,
        freeJobCode = "",
        premiumRatePercentLabel = "۱۸ درصد",
        deferredDebtLabel = "۵۵٬۶۶۲٬۳۴۱ ریال",
    ),
    ContractPR(
        contractNumber = "4841110073",
        statusDesc = "در انتظار بررسی",
        isActive = true,
        requestDate = "۱۴۰۵/۰۵/۱۸",
        insuranceType = "بیمهٔ زنان خانه‌دار",
        monthlyPremiumLabel = "زنان خانه‌دار ۱۴ درصد",
        monthlyIncome = "110000000",
        treatmentSupportText = "حمایت درمان ندارد",
        hasTreatmentSupport = false,
        jobTitle = "",
        premiumTypeCode = "05",
        statusCode = null,
        freeJobCode = "",
        premiumRatePercentLabel = "۱۴ درصد",
    ),
).toImmutableList()
