package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.ContractRowsUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesErrorSource
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesIntent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesScreen
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerContractRowCard
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerContractRowsHeader
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesErrorView
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesListSkeleton
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerWorkshopInfoCard
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.paging.OnLoadMore
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_contract_rows_empty_subtitle
import taminx.core.core_ui.employer_online_services_contract_rows_empty_title

/** How far the workshop card hangs below the header gradient — mirrors the landing identity card. */
private val WorkshopCardOverhang = 44.dp

/**
 * "ردیف‌های پیمان کارگاه" — the per-workshop contract-rows drill-down, opened from a card's
 * "ردیف‌های پیمان" chip. Same shell as the landing screen: pinned gradient header with an identity
 * card overlapping its bottom edge, a `reservedHeight` spacer, then the list / skeleton / empty /
 * error states. Loading and errors are driven by the shared ViewModel
 * ([EmployerOnlineServicesErrorSource.CONTRACT_ROWS]).
 */
@Composable
internal fun EmployerContractRowsScreen(
    uiState: EmployerOnlineServicesUiState,
    onIntent: (EmployerOnlineServicesIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    var headerHeightPx by remember { mutableIntStateOf(0) }

    val rows = uiState.contractRows
    val error = uiState.errors[EmployerOnlineServicesErrorSource.CONTRACT_ROWS]
    val listState = rememberLazyListState()

    listState.OnLoadMore(
        enabled = !rows.endReached && rows.paginationError == null,
    ) {
        onIntent(EmployerOnlineServicesIntent.LoadMoreContractRows)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            state = listState,
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            }

            when {
                uiState.isLoading && rows.rows.isEmpty() -> item {
                    EmployerOnlineServicesListSkeleton()
                }

                error != null && rows.rows.isEmpty() -> item {
                    EmployerOnlineServicesErrorView(
                        error = error,
                        onRetry = {
                            onIntent(
                                EmployerOnlineServicesIntent.RetrySource(
                                    EmployerOnlineServicesErrorSource.CONTRACT_ROWS,
                                ),
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                    )
                }

                rows.rows.isEmpty() -> item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Description,
                        title = stringResource(Res.string.employer_online_services_contract_rows_empty_title),
                        subtitle = stringResource(Res.string.employer_online_services_contract_rows_empty_subtitle),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }

                else -> {
                    itemsIndexed(
                        rows.rows,
                        key = { index, item -> item.contractRow + "-" + index },
                    ) { _, item ->
                        EmployerContractRowCard(
                            item = item,
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }

                    item {
                        PagingFooter(
                            isLoadingNextPage = rows.isLoadingNextPage,
                            error = rows.paginationError,
                            onRetry = { onIntent(EmployerOnlineServicesIntent.LoadMoreContractRows) },
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }
                }
            }
        }

        // Pinned header + workshop card overlapping its bottom edge, measured as one block.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onSizeChanged { headerHeightPx = it.height },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                EmployerContractRowsHeader(
                    onBackClicked = onBackClicked,
                    contractRowsUiState = rows
                )
                Spacer(modifier = Modifier.height(WorkshopCardOverhang))
            }
        }

        if (uiState.isLoading && rows.rows.isNotEmpty()) {
            LoadingStateOverlay()
        }
    }
}

// ----------------------------------------------------------------------- previews

private val PreviewRows = persistentListOf(
    WorkshopContractRowPR(
        contractRow = "۰۰۱",
        fullName = "حسین توکلی کرمانی",
        nationalCode = "۴۴۷۹۸۹۰۸۸۲",
        mobile = "۰۹۱۵۳۲۱۴۴۷۸",
        email = "info@damabokhar.ir",
        tel = "۰۵۱۳۷۶۵۴۳۲۱",
        postalCode = "۸۴۵۲۱",
        startDate = "۱۴۰۳/۰۵/۱۹",
        endDate = "۱۴۰۵/۰۵/۱۸",
        workshopName = "شرکت صنایع دما بخار مشهد",
        workshopCodeLabel = "۰۰۸۱۶۳۱۸۲۹",
    ),
    WorkshopContractRowPR(
        contractRow = "۰۰۲",
        fullName = "مریم توکلی",
        nationalCode = "۰۹۲۳۴۴۷۱۲۰",
        mobile = "۰۹۱۵۱۱۰۲۲۳۴",
        email = "projects@damabokhar.ir",
        tel = "۰۵۱۳۷۶۵۴۳۲۲",
        postalCode = "۸۴۵۳۹",
        startDate = "۱۴۰۴/۰۱/۱۵",
        endDate = "۱۴۰۵/۱۲/۲۹",
        workshopName = "شرکت صنایع دما بخار مشهد",
        workshopCodeLabel = "۰۰۸۱۶۳۱۸۲۹",
    ),
)

private val PreviewLoadedState = EmployerOnlineServicesUiState(
    currentScreen = EmployerOnlineServicesScreen.CONTRACT_ROWS,
    contractRows = ContractRowsUiState(
        workshopName = "شرکت صنایع دما بخار مشهد",
        workshopCodeLabel = "۰۰۸۱۶۳۱۸۲۹",
        workshopId = "0081631829",
        branchCode = "1202",
        rows = PreviewRows,
    ),
)

@PreviewRtlTheme
@Composable
private fun EmployerContractRowsScreenPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            EmployerContractRowsScreen(
                uiState = PreviewLoadedState,
                onIntent = {},
                onBackClicked = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerContractRowsScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            EmployerContractRowsScreen(
                uiState = PreviewLoadedState,
                onIntent = {},
                onBackClicked = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerContractRowsScreenEmptyPreview() {
    PreviewRtlThemeContent {
        AppToastHost {
            EmployerContractRowsScreen(
                uiState = PreviewLoadedState.copy(
                    contractRows = PreviewLoadedState.contractRows.copy(rows = persistentListOf()),
                ),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerContractRowsScreenLoadingPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            EmployerContractRowsScreen(
                uiState = PreviewLoadedState.copy(
                    isLoading = true,
                    contractRows = PreviewLoadedState.contractRows.copy(rows = persistentListOf()),
                ),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}
