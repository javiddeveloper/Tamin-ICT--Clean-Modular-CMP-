package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.tamin.taminhamrah.ui.components.TaminPageIndicator
import androidx.compose.ui.layout.onSizeChanged
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItemPR
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentFeatureFlags
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMessageType
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toCardItems
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientList
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.util.ExternalAppLauncher
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.deep_link_feature_unavailable
import taminx.core.core_ui.patient_dependant_relation
import taminx.core.core_ui.patient_main_insured_fallback

/**
 * Treatment hub: the insured person's electronic health-insurance cards, the quick-access
 * destinations, and the service categories.
 */
@Composable
fun TreatmentScreen(
    viewModel: TreatmentViewModel = koinViewModel(),
    onOpenMedicalRecords: (nationalCode: String) -> Unit = {},
    onOpenHealthProfile: (nationalCode: String) -> Unit = {},
    onOpenPrescriptions: (String) -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
    onOpenApprovals: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto-fetch data on composition entry
    LaunchedEffect(Unit) {
        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    // The contracted-centers directory is a web page the organization maintains, not a screen of
    // ours, so it opens in the browser on both platforms.
    val centersLauncher = remember { ExternalAppLauncher() }
    HandleTreatmentEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onNavigateToRecords = { nationalCode, tab ->
            if (tab == RecordTab.MEDICINE) onOpenPrescriptions(nationalCode)
            else onOpenMedicalRecords(nationalCode)
        },
        onNavigateToMiscClaims = onOpenMiscClaims,
        onNavigateToApprovals = onOpenApprovals,
        onNavigateToHealthProfile = onOpenHealthProfile,
        onNavigateToContractedCenters = { centersLauncher.openUrl(CONTRACTED_CENTERS_URL) },
    )

    Box(modifier = Modifier.fillMaxSize()) {
        TreatmentContent(
            state = uiState,
            onIntent = viewModel::sendIntent,
        )
        // Overlaid rather than wrapped in a Scaffold so the hub keeps its edge-to-edge header.
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}


@Composable
fun HandleTreatmentEvents(
    events: Flow<TreatmentEvent>,
    snackbarHostState: SnackbarHostState,
    onNavigateToRecords: (String, RecordTab) -> Unit,
    onNavigateToMiscClaims: () -> Unit = {},
    onNavigateToApprovals: () -> Unit = {},
    onNavigateToHealthProfile: (nationalCode: String) -> Unit = {},
    onNavigateToContractedCenters: () -> Unit = {},
) {
    events.collectWithLifecycleAware {
        when (it) {
            // Navigation is an event because the feature flag decides it, not the tap.
            is TreatmentEvent.NavigateToRecords -> onNavigateToRecords(it.nationalCode, it.tab)
            TreatmentEvent.NavigateToMiscClaims -> onNavigateToMiscClaims()
            TreatmentEvent.NavigateToApprovals -> onNavigateToApprovals()
            is TreatmentEvent.NavigateToHealthProfile -> onNavigateToHealthProfile(it.nationalCode)
            TreatmentEvent.NavigateToContractedCenters -> onNavigateToContractedCenters()

            // A gated feature explains itself here; a silent gate would look like a dead button.
            is TreatmentEvent.ShowMessage -> snackbarHostState.showSnackbar(
                message = it.message,
                withDismissAction = it.type == TreatmentMessageType.OPERATION_FAILED,
                duration = if (it.type == TreatmentMessageType.OPERATION_FAILED) {
                    SnackbarDuration.Long
                } else {
                    SnackbarDuration.Short
                },
            )
        }
    }
}

/**
 * Every tile raises an intent, not a callback: the feature flag in the view model decides whether
 * that becomes a navigation event, and the navigation lambdas belong to [TreatmentScreen], which
 * handles those events.
 */
@Composable
fun TreatmentContent(
    state: TreatmentUiState,
    onIntent: (TreatmentIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Keyed on the data the cards are built from, not on the whole state: selecting a patient
    // must not rebuild the list, or every swipe would invalidate the carousel and its effects.
    val mainInsuredFallback = stringResource(Res.string.patient_main_insured_fallback)
    val dependantRelation = stringResource(Res.string.patient_dependant_relation)
    val patients = remember(
        state.mainUserNationalCode,
        state.deservedList,
        state.dependantList,
        mainInsuredFallback,
        dependantRelation,
    ) {
        state.toPatientList(mainInsuredFallback, dependantRelation)
    }
    val cards = remember(patients, state.deservedList) { patients.toCardItems(state.deservedList) }
    val pagerState = rememberPagerState(pageCount = { cards.size })
    val scrollState = rememberScrollState()

    SyncPagerWithSelection(
        selectedNationalCode = state.selectedNationalCode,
        patients = patients,
        pagerState = pagerState,
        onIntent = onIntent,
    )

    val collapse = rememberCollapsingHeaderState(TreatmentDimens.headerCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }

    val handleOpenMedicalRecords = remember(onIntent) { { onIntent(TreatmentIntent.OpenRecords(RecordTab.Default)) } }
    val handleOpenHealthProfile = remember(onIntent) { { onIntent(TreatmentIntent.OpenHealthProfile) } }
    val handleOpenPrescriptions = remember(onIntent) { { onIntent(TreatmentIntent.OpenRecords(RecordTab.MEDICINE)) } }
    val handleOpenMiscClaims = remember(onIntent) { { onIntent(TreatmentIntent.OpenMiscClaims) } }
    val handleOpenApprovals = remember(onIntent) { { onIntent(TreatmentIntent.OpenApprovals) } }
    val handleOpenCenters = remember(onIntent) { { onIntent(TreatmentIntent.OpenContractedCenters) } }
    val handleRetry = remember(onIntent) { { onIntent(TreatmentIntent.InitTreatmentFlow) } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalTaminColors.current.bgPage),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection)
                .verticalScroll(scrollState, overscrollEffect = rememberJellyOverscroll()),
        ) {
            Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            // The pager's dots belong to the page, not to the floating header above it: inside the
            // header they stay put while the page moves, and content slides behind them.
            if (cards.size > 1) {
                TaminPageIndicator(
                    pageCount = cards.size,
                    pagerState = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TreatmentDimens.pageIndicatorTopGap),
                )
            }
            // Tighter than the gaps between the sections below: the carousel above already ends in
            // its own margin, and a full section gap on top of it read as a hole.
            Spacer(modifier = Modifier.height(Spacing.sm))
            TreatmentQuickAccess(
                healthProfileCompleted = state.healthProfileCompleted,
                recordsStatus = state.featureStatuses?.get(TreatmentFeatureFlags.records),
                healthProfileStatus = state.featureStatuses?.get(TreatmentFeatureFlags.healthProfile),
                centersStatus = state.featureStatuses?.get(TreatmentFeatureFlags.contractedCenters),
                onOpenMedicalRecords = handleOpenMedicalRecords,
                onOpenHealthProfile = handleOpenHealthProfile,
                onOpenCenters = handleOpenCenters,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCategories(
                prescriptionsStatus = state.featureStatuses?.get(TreatmentFeatureFlags.prescriptions),
                approvalsStatus = state.featureStatuses?.get(TreatmentFeatureFlags.approvals),
                miscClaimsStatus = state.featureStatuses?.get(TreatmentFeatureFlags.miscClaims),
                onOpenPrescriptions = handleOpenPrescriptions,
                onOpenMiscClaims = handleOpenMiscClaims,
                onOpenApprovals = handleOpenApprovals,
            )
            // Hidden once the flag has actually answered off; unresolved (null) still shows it as
            // loading, the same as every gated entry above.
            val costsStatus = state.featureStatuses?.get(TreatmentFeatureFlags.currentYearCosts)
            if (costsStatus?.opensSomething != false) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                TreatmentCostSummary(
                    insuredShare = state.insuredShareTotal,
                    organizationShare = state.organizationShareTotal,
                    isLoading = state.isLoading || costsStatus == null,
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xxl + TreatmentDimens.cardOverlap))
        }


        // The header floats on top so that as content scrolls up, it passes underneath the header.
        TreatmentHubHeader(
            progress = collapse.progressProvider,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        ) {
            val cardStatus = state.featureStatuses?.get(TreatmentFeatureFlags.insuranceCard)
            PatientCarousel(
                cards = cards,
                // The card is not drawn before the menu has said it may be.
                isLoading = state.isLoading || cardStatus == null,
                unavailableMessage = cardStatus?.takeUnless { it.opensSomething }?.let {
                    it.serverMessage ?: stringResource(Res.string.deep_link_feature_unavailable)
                },
                error = state.error,
                pagerState = pagerState,
                onRetry = handleRetry,
                collapseProgress = collapse.progressProvider,
                showIndicator = false,
            )
        }
    }

}

/**
 * Keeps the pager and the selected national code in step in both directions: a restored
 * selection scrolls the pager, and a swipe reports the new selection back.
 */
@Composable
private fun SyncPagerWithSelection(
    selectedNationalCode: String?,
    patients: ImmutableList<PatientItemPR>,
    pagerState: PagerState,
    onIntent: (TreatmentIntent) -> Unit,
) {
    LaunchedEffect(selectedNationalCode, patients) {
        val index = patients.indexOfFirst { it.nationalId == selectedNationalCode }
        if (index >= 0 && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }

    // The page is watched through a snapshot flow rather than an effect key, so a swipe does not
    // recompose anything on the way to reporting the new selection.
    val currentSelection by rememberUpdatedState(selectedNationalCode)
    LaunchedEffect(patients) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val selected = patients.getOrNull(page) ?: return@collect
            if (selected.nationalId != currentSelection) {
                onIntent(TreatmentIntent.SelectPatient(selected.nationalId, selected.fullName))
            }
        }
    }
}

/**
 * The organization's directory of contracted treatment centres.
 *
 * A page on tamin.ir rather than an endpoint: there is no centers API, and the published list is
 * what the branches actually keep current.
 */
private const val CONTRACTED_CENTERS_URL = "https://tamin.ir/html/item/4474"

@PreviewRtlTheme
@Composable
fun TreatmentScreenPreview() {
    PreviewRtlThemeContent {
        TreatmentContent(
            state = TreatmentMocks.mainUiState,
            onIntent = {},
        )
    }
}
