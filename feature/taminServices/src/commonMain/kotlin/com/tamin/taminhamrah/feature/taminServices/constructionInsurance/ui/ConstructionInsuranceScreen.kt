package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionInsuranceAction
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionFileCard
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionInsuranceHeader
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionInsuranceListSkeleton
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionSearchCard
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionSearchEmptyState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionSearchFilterChipRow
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionUserInfoCard
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.no_construction_files_found
import taminx.core.core_ui.Res as CoreRes

@Composable
fun ConstructionInsuranceRoute(
    viewModel: ConstructionInsuranceViewModel,
    onBackClicked: () -> Unit,
    onNavigateToViewDetail: (fileNumber: Long?, requestNumber: Long?) -> Unit,
    onNavigateToPaymentSheet: (debitNumber: String, branchCode: String) -> Unit,
    onNavigateToInstallmentLetter: (workshopId: String, branchId: String) -> Unit,
    onNavigateToBeneficiaries: (requestNumber: Long?, fileNumber: Long?, requestDate: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    ConstructionInsuranceEvents(
        events = viewModel.events,
        toaster = toaster,
        onNavigateToViewDetail = onNavigateToViewDetail,
    )

    ConstructionInsuranceScreen(
        state = uiState,
        onBackClicked = onBackClicked,
        onIntent = viewModel::sendIntent,
        onNavigateToViewDetail = onNavigateToViewDetail,
        onNavigateToPaymentSheet = onNavigateToPaymentSheet,
        onNavigateToInstallmentLetter = onNavigateToInstallmentLetter,
        onNavigateToBeneficiaries = onNavigateToBeneficiaries,
        modifier = modifier
    )
}

@Composable
fun ConstructionInsuranceEvents(
    events: Flow<ConstructionInsuranceEvent>,
    toaster: ToasterState,
    onNavigateToViewDetail: (fileNumber: Long?, requestNumber: Long?) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ConstructionInsuranceEvent.ShowToast -> {
                toaster.error(event.message)
            }

            is ConstructionInsuranceEvent.NavigateToDetails -> {
                onNavigateToViewDetail(event.item.fileNumber, event.item.requestNumber)
            }
        }
    }
}

@Composable
fun ConstructionInsuranceScreen(
    state: ConstructionInsuranceState,
    onBackClicked: () -> Unit,
    onIntent: (ConstructionInsuranceIntent) -> Unit,
    onNavigateToViewDetail: (fileNumber: Long?, requestNumber: Long?) -> Unit = { _, _ -> },
    onNavigateToPaymentSheet: (debitNumber: String, branchCode: String) -> Unit = { _, _ -> },
    onNavigateToInstallmentLetter: (workshopId: String, branchId: String) -> Unit = { _, _ -> },
    onNavigateToBeneficiaries: (requestNumber: Long?, fileNumber: Long?, requestDate: String?) -> Unit =
        { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val hasActiveFilter = state.appliedFileNoQuery.isNotBlank() ||
        state.appliedReqNoQuery.isNotBlank() ||
        state.appliedWorkshopIdQuery.isNotBlank() ||
        state.appliedBranchCodeQuery.isNotBlank()

    // Folds the header's ring icon + subtitle from the list's own drag, snapping to open/closed on
    // release — the user-info card below it stays fully shown, pinned above the list. The drag
    // budget is measured from this exact header + card block. See docs/vault/TopArea-System.md
    // (same shape as feature/taminServices' EmployerOnlineServicesScreen).
    val hasIdentity = state.userName.isNotBlank() || state.nationalCode.isNotBlank()
    val topArea = rememberMeasuredTopAreaState(key = hasIdentity) { probeState ->
        ConstructionInsuranceTopArea(
            userName = state.userName,
            nationalCode = state.nationalCode,
            onBackClicked = onBackClicked,
            topAreaState = probeState,
        )
    }
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
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
                rest = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            overscrollEffect = rememberJellyOverscroll(),
        ) {
            item {
                ConstructionSearchCard(
                    itemCount = state.items.size,
                    isExpanded = state.isSearchExpanded,
                    isNoticeVisible = state.isNoticeVisible,
                    fileNoQuery = state.fileNoQuery,
                    reqNoQuery = state.reqNoQuery,
                    workshopIdQuery = state.workshopIdQuery,
                    branchCodeQuery = state.branchCodeQuery,
                    onToggleExpanded = {
                        onIntent(
                            ConstructionInsuranceIntent.ToggleSearchExpanded(
                                it
                            )
                        )
                    },
                    onFileNoChanged = {
                        onIntent(
                            ConstructionInsuranceIntent.OnFileNoQueryChanged(
                                it
                            )
                        )
                    },
                    onReqNoChanged = {
                        onIntent(
                            ConstructionInsuranceIntent.OnReqNoQueryChanged(
                                it
                            )
                        )
                    },
                    onWorkshopIdChanged = {
                        onIntent(
                            ConstructionInsuranceIntent.OnWorkshopIdQueryChanged(
                                it
                            )
                        )
                    },
                    onBranchCodeChanged = {
                        onIntent(
                            ConstructionInsuranceIntent.OnBranchCodeQueryChanged(
                                it
                            )
                        )
                    },
                    onExecuteSearch = { onIntent(ConstructionInsuranceIntent.ExecuteSearch) },
                    onResetSearch = { onIntent(ConstructionInsuranceIntent.ResetSearch) },
                    onInfoIconClicked = {
                        onIntent(ConstructionInsuranceIntent.ToggleNoticeVisibility)
                    }
                )
            }

            if (state.isLoading && state.items.isEmpty()) {
                item {
                    ConstructionInsuranceListSkeleton(modifier = Modifier.padding(top = Spacing.sm))
                }
            }
            if (!state.isLoading && state.error == null) {
                if (hasActiveFilter) {
                    item {
                        ConstructionSearchFilterChipRow(
                            fileNoQuery = state.appliedFileNoQuery,
                            reqNoQuery = state.appliedReqNoQuery,
                            workshopIdQuery = state.appliedWorkshopIdQuery,
                            branchCodeQuery = state.appliedBranchCodeQuery,
                            onClear = { onIntent(ConstructionInsuranceIntent.ResetSearch) },
                        )
                    }
                }

                items(
                    items = state.items,
                    key = { it.fileNumber ?: it.hashCode() }
                ) { file ->
                    ConstructionFileCard(
                        item = file,
                        onDetailClick = { onIntent(ConstructionInsuranceIntent.OnDetailClick(file)) },
                        onActionSelect = { action ->
                            when (action) {
                                ConstructionInsuranceAction.PaymentSheet ->
                                    onNavigateToPaymentSheet(
                                        file.debitNumber.orEmpty(),
                                        file.workshopInfo?.brhCode.orEmpty(),
                                    )

                                ConstructionInsuranceAction.InstallmentLetter ->
                                    onNavigateToInstallmentLetter(
                                        file.workshopInfo?.workshopId.orEmpty(),
                                        file.workshopInfo?.brhCode.orEmpty(),
                                    )

                                ConstructionInsuranceAction.Beneficiaries ->
                                    onNavigateToBeneficiaries(file.requestNumber, file.fileNumber, file.requestDate)
                            }
                        },
                    )
                }
            }

            if (!state.isLoading && state.error == null && state.items.isEmpty()) {
                item {
                    if (hasActiveFilter) {
                        ConstructionSearchEmptyState(modifier = Modifier.padding(top = Spacing.md))
                    } else {
                        TaminEmptyState(
                            message = stringResource(CoreRes.string.no_construction_files_found),
                            modifier = Modifier.padding(top = Spacing.xxl),
                        )
                    }
                }
            }
        }

        // The real, interactive top area floats over the list and reports its own rendered height
        // back so the list reserves exactly the space it occupies.
        ConstructionInsuranceTopArea(
            userName = state.userName,
            nationalCode = state.nationalCode,
            onBackClicked = onBackClicked,
            topAreaState = topArea,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )

        if (state.isLoading && state.items.isNotEmpty()) {
            LoadingStateOverlay()
        }
    }
}

/** How much the user-info card rides up into the header gradient, straddling the seam. */
private val UserInfoCardOverhang = 40.dp

/**
 * The list screen's floating top area: the folding gradient header (back, ring icon, subtitle)
 * with the کاربر identity card riding up [UserInfoCardOverhang] into its gradient to straddle the
 * seam. Composed twice — once off-screen by [rememberMeasuredTopAreaState] to measure the fold
 * budget, once for real over the list with [reportTopAreaHeight]. Mirrors
 * `EmployerOnlineServicesScreen`'s `EmployerOnlineServicesTopArea`.
 */
@Composable
private fun ConstructionInsuranceTopArea(
    userName: String,
    nationalCode: String,
    onBackClicked: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        ConstructionInsuranceHeader(
            onBackClicked = onBackClicked,
            topAreaState = topAreaState,
            heroCardOverlap = UserInfoCardOverhang,
        )
        ConstructionUserInfoCard(
            userName = userName,
            nationalCode = nationalCode,
            // Rides up into the header's reserved bottom space so it straddles the seam, and
            // reports a height reduced by the same overlap so reportTopAreaHeight sees the true
            // footprint instead of counting the overlap twice as reserved list space.
            modifier = Modifier
                .straddlePreviousSibling(UserInfoCardOverhang)
                .padding(horizontal = Spacing.page)
                .coloredShadow(
                    color = taminColors.shadowSubtle,
                    borderRadius = CornerRadius.lg,
                    blurRadius = Elevation.lg,
                    offsetY = Spacing.xs,
                ),
        )
        Spacer(Modifier.height(Spacing.sm))
    }
}

/**
 * Shifts this child up by [overlap] to overlap the previous sibling's bottom edge, while reporting
 * a height reduced by that same amount — so a parent measuring total column height (here,
 * [reportTopAreaHeight]) sees the true visual footprint instead of double-counting the overlap.
 * Same helper as `EmployerOnlineServicesScreen`/`LegalRepresentativeWorkshopsScreen`, independently
 * re-declared file-local here too rather than extracted — see docs/vault/TopArea-System.md.
 */
private fun Modifier.straddlePreviousSibling(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val overlapPx = overlap.roundToPx()
    val reportedHeight = (placeable.height - overlapPx).coerceAtLeast(0)
    layout(placeable.width, reportedHeight) {
        placeable.placeRelative(0, -overlapPx)
    }
}

private val previewItems = persistentListOf(
    ConstructionFilePR(
        fileNumber = 4479890882L,
        requestNumber = 123456789L,
        requestDate = "14020901",
        workshopInfo = WorkshopIdInfoPR(
            workshopRegisterDate = "14020901",
            workshopId = "9028222442",
            brhCode = "6400"
        ),
        postalCode = "9187955511",
        address = "مشهد - بلوار وکیل آباد",
        mainPlaque = 12,
        subPlaque = 3,
        block = 5L,
        propertyConstruction = 1400,
        apartment = 2,
        trade = 1,
        partPlaque = 4,
        sumOfComplications = 250_000L,
        debitNumber = "123456789012",
        totalPayment = 1_850_000L,
        meterage = 120,
        debitStatusCode = "51",
        protrusion = 50_000L,
        applicationFees = 200_000L,
        residentialServiceInfrastructureFees = 150_000L,
        excessDensitySurchargeFees = 100_000L,
        increasePropertyValue = 300_000L,
        issuanceFencingWallConstructionFees = 80_000L,
        coveredClause3Fees = 60_000L,
        article100 = 40_000L,
        paymentDeadLine = "14021001"
    ),
    ConstructionFilePR(
        fileNumber = 4479890883L,
        requestNumber = 123456790L,
        requestDate = "14020815",
        workshopInfo = WorkshopIdInfoPR(
            workshopRegisterDate = "14020815",
            workshopId = "6393610019",
            brhCode = "6400"
        ),
        postalCode = "9187955512",
        address = "مشهد - احمدآباد",
        mainPlaque = 45,
        subPlaque = 1,
        block = 2L,
        propertyConstruction = 1395,
        apartment = 4,
        trade = 0,
        partPlaque = 2,
        sumOfComplications = 180_000L,
        debitNumber = null,
        totalPayment = 1_200_000L,
        meterage = 95,
        debitStatusCode = "10",
        protrusion = 30_000L,
        applicationFees = 150_000L,
        residentialServiceInfrastructureFees = 120_000L,
        excessDensitySurchargeFees = 80_000L,
        increasePropertyValue = 220_000L,
        issuanceFencingWallConstructionFees = 60_000L,
        coveredClause3Fees = 40_000L,
        article100 = 20_000L,
        paymentDeadLine = "14020920"
    ),
)

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenPreview() {
    PreviewRtlThemeContent {
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = previewItems,
            ),
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = previewItems,
            ),
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenEmptyPreview() {
    PreviewRtlThemeContent {
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = emptyList<ConstructionFilePR>().toImmutableList(),
            ),
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenLoadingPreview() {
    PreviewRtlThemeContent {
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                isLoading = true,
                items = emptyList<ConstructionFilePR>().toImmutableList(),
            ),
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenSearchEmptyPreview() {
    PreviewRtlThemeContent {
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = emptyList<ConstructionFilePR>().toImmutableList(),
                fileNoQuery = "4479890882",
                workshopIdQuery = "9028222442",
                appliedFileNoQuery = "4479890882",
                appliedWorkshopIdQuery = "9028222442",
            ),
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenSearchResultsPreview() {
    PreviewRtlThemeContent {
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = previewItems,
                fileNoQuery = "4479890882",
                appliedFileNoQuery = "4479890882",
            ),
            onBackClicked = {},
            onIntent = {}
        )
    }
}
