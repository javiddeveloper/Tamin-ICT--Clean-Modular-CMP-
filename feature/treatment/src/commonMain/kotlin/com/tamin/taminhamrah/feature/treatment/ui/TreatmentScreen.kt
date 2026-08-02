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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
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
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.coverage_reason_dialog_title
import taminx.core.core_ui.patient_dependant_relation
import taminx.core.core_ui.patient_main_insured_fallback
import org.jetbrains.compose.resources.stringResource

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
    var entitlementReason by remember { mutableStateOf<String?>(null) }
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

    // Hoisted so the section lambdas below capture one string rather than the whole state —
    // capturing `state` would make them a new instance on every load and defeat skipping.
    val mainUserNationalCode = state.mainUserNationalCode

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
                // Records are feature-flag gated, so the tap fires an intent; the emitted
                // NavigateToRecords event carries the selected patient's national code.
                onOpenMedicalRecords = { onIntent(TreatmentIntent.OpenRecords(RecordTab.Default)) },
                // "پروندهٔ سلامت من" is always the main insured person's profile, regardless of
                // which patient card is in view. No-op until the main code is known.
                onOpenHealthProfile = { mainUserNationalCode?.let(onOpenHealthProfile) },
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCategories(
                onOpenPrescriptions = { onIntent(TreatmentIntent.OpenRecords(RecordTab.MEDICINE)) },
                // Not feature-flag gated like the records, so the tap navigates straight away.
                onOpenMiscClaims = onOpenMiscClaims,
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
                onShowEntitlementReason = { entitlementReason = it },
                onRetry = { onIntent(TreatmentIntent.InitTreatmentFlow) },
                collapseProgress = collapse.progressProvider,
            )
        }
    }

    entitlementReason?.let { reason ->
        EntitlementReasonDialog(
            reason = reason,
            onDismiss = { entitlementReason = null },
        )
    }
}

/**
 * Keeps the pager and the selected national code in step in both directions: a restored
 * selection scrolls the pager, and a swipe reports the new selection back.
 */
@Composable
private fun SyncPagerWithSelection(
    selectedNationalCode: String?,
    patients: ImmutableList<PatientItem>,
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

@Composable
private fun EntitlementReasonDialog(
    reason: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_confirm)) }
        },
        title = { Text(stringResource(Res.string.coverage_reason_dialog_title)) },
        text = { Text(reason) },
    )
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
