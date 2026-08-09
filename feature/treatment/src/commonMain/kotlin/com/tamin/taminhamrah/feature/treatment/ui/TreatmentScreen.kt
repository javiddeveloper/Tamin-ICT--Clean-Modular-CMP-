package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.onSizeChanged
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItemPR
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
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
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto-fetch data on composition entry
    LaunchedEffect(Unit) {
        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    HandleTreatmentEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onNavigateToRecords = { nationalCode, tab ->
            if (tab == RecordTab.MEDICINE) onOpenPrescriptions(nationalCode)
            else onOpenMedicalRecords(nationalCode)
        },
    )

    Box(modifier = Modifier.fillMaxSize()) {
        TreatmentContent(
            state = uiState,
            onIntent = viewModel::sendIntent,
            onOpenMedicalRecords = onOpenMedicalRecords,
            onOpenHealthProfile = onOpenHealthProfile,
            onOpenPrescriptions = onOpenPrescriptions,
            onOpenMiscClaims = onOpenMiscClaims,
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
) {
    events.collectWithLifecycleAware {
        when (it) {
            // Navigation is an event because the feature flag decides it, not the tap.
            is TreatmentEvent.NavigateToRecords -> onNavigateToRecords(it.nationalCode, it.tab)

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

@Composable
fun TreatmentContent(
    state: TreatmentUiState,
    onIntent: (TreatmentIntent) -> Unit,
    modifier: Modifier = Modifier,
    onOpenMedicalRecords: (nationalCode: String) -> Unit = {},
    onOpenHealthProfile: (nationalCode: String) -> Unit = {},
    onOpenPrescriptions: (String) -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
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

    // Folds the header from the body's drag (before the body scrolls), snapping on release. Read
    // only inside the card morph's layout/draw lambdas, so the fold never recomposes the hub.
    val collapse = rememberCollapsingHeaderState(TreatmentDimens.headerCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }

    // =================================================================================
    // MEMOIZED CALLBACK LAMBDAS
    // Hoisted and wrapped in `remember` so child composables (TreatmentQuickAccess, TreatmentCategories)
    // receive stable function references and completely skip recomposition when patient cards are swiped.
    // =================================================================================
    val mainUserNationalCode = state.mainUserNationalCode
    val currentOnOpenHealthProfile by rememberUpdatedState(onOpenHealthProfile)
    val currentOnOpenMiscClaims by rememberUpdatedState(onOpenMiscClaims)

    val handleOpenMedicalRecords = remember(onIntent) { { onIntent(TreatmentIntent.OpenRecords(RecordTab.Default)) } }
    val handleOpenHealthProfile = remember(mainUserNationalCode) {
        { mainUserNationalCode?.let { currentOnOpenHealthProfile(it) } ?: Unit }
    }
    val handleOpenPrescriptions = remember(onIntent) { { onIntent(TreatmentIntent.OpenRecords(RecordTab.MEDICINE)) } }
    val handleOpenMiscClaims = remember { { currentOnOpenMiscClaims() } }
    val handleRetry = remember(onIntent) { { onIntent(TreatmentIntent.InitTreatmentFlow) } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalTaminColors.current.bgPage),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // The body's drag first folds the header, then scrolls the sections, and only what
                // neither wanted reaches the rubber band — so the fold always wins over the bounce.
                .nestedScroll(collapse.nestedScrollConnection)
                .verticalScroll(scrollState, overscrollEffect = rememberJellyOverscroll()),
        ) {
            // Stands in for the floating header, which is measured rather than fixed.
            Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentQuickAccess(
                healthProfileCompleted = state.healthProfileCompleted,
                onOpenMedicalRecords = handleOpenMedicalRecords,
                onOpenHealthProfile = handleOpenHealthProfile,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCategories(
                onOpenPrescriptions = handleOpenPrescriptions,
                onOpenMiscClaims = handleOpenMiscClaims,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCostSummary(
                insuredShare = state.insuredShareTotal,
                organizationShare = state.organizationShareTotal,
                isLoading = state.isLoading,
            )
            // Clears the floating navigation bar, as the pre-collapse layout did.
            Spacer(modifier = Modifier.height(Spacing.xxl + TreatmentDimens.cardOverlap))
        }

        // The header floats on top so that as content scrolls up, it passes underneath the header.
        TreatmentHubHeader(
            progress = collapse.progressProvider,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        ) {
            PatientCarousel(
                cards = cards,
                isLoading = state.isLoading,
                error = state.error,
                pagerState = pagerState,
                onRetry = handleRetry,
                collapseProgress = collapse.progressProvider,
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
