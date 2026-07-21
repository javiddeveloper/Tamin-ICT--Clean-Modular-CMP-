package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMessageType
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientList
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

/** How far the insured-person carousel rides up into the teal header. */
private val CARD_OVERLAP = 40.dp

/**
 * Treatment hub: the insured person's electronic health-insurance cards, the quick-access
 * destinations, and the service categories.
 */
@Composable
fun TreatmentScreen(
    viewModel: TreatmentViewModel = koinViewModel(),
    onOpenMedicalRecords: (String) -> Unit = {},
    onOpenHealthProfile: () -> Unit = {},
    onOpenCenters: () -> Unit = {},
    onOpenPrescriptions: (String) -> Unit = {},
    onOpenMedicalApprovals: () -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
    onSearch: () -> Unit = {},
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
        onOpenCenters = onOpenCenters,
        onOpenPrescriptions = onOpenPrescriptions,
        onOpenMedicalApprovals = onOpenMedicalApprovals,
        onOpenMiscClaims = onOpenMiscClaims,
        onSearch = onSearch,
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
    onOpenMedicalRecords: (String) -> Unit = {},
    onOpenHealthProfile: () -> Unit = {},
    onOpenCenters: () -> Unit = {},
    onOpenPrescriptions: (String) -> Unit = {},
    onOpenMedicalApprovals: () -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
    onSearch: () -> Unit = {},
) {
    val patients = remember(state) { state.toPatientList() }
    var entitlementReason by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState(pageCount = { patients.size })

    SyncPagerWithSelection(
        state = state,
        patients = patients,
        pagerState = pagerState,
        onIntent = onIntent,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LocalTaminColors.current.bgPage)
            .verticalScroll(rememberScrollState()),
    ) {
        TreatmentHubHeader(onSearch = onSearch)
        // The whole body shifts up together, so the overlap does not leave a gap below.
        Column(modifier = Modifier.offset(y = -CARD_OVERLAP)) {
            PatientCarousel(
                state = state,
                patients = patients,
                pagerState = pagerState,
                onShowEntitlementReason = { entitlementReason = it },
                onRetry = { onIntent(TreatmentIntent.InitTreatmentFlow) },
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentQuickAccess(
                healthProfileCompleted = state.healthProfileCompleted,
                // Nothing to open until a patient is selected.
                onOpenMedicalRecords = { onIntent(TreatmentIntent.OpenRecords(RecordTab.ALL)) },
                onOpenHealthProfile = onOpenHealthProfile,
                onOpenCenters = onOpenCenters,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCategories(
                onOpenPrescriptions = { onIntent(TreatmentIntent.OpenRecords(RecordTab.MEDICINE)) },
                onOpenMedicalApprovals = onOpenMedicalApprovals,
                onOpenMiscClaims = onOpenMiscClaims,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCostSummary(
                insuredShare = state.insuredShareTotal,
                organizationShare = state.organizationShareTotal,
            )
            Spacer(modifier = Modifier.height(Spacing.xxl + CARD_OVERLAP))
        }
    }

    entitlementReason?.let { reason ->
        EntitlementReasonDialog(
            reason = reason,
            onDismiss = { entitlementReason = null },
        )
    }
}

@Composable
private fun TreatmentHubHeader(onSearch: () -> Unit) {
    TaminTopAppBar(
        title = "درمان",
        centerTitle = false,
        // Runs deep enough for the carousel to ride up into it. The gradient and the
        // status-bar fill are left at their defaults, which is what puts the visible
        // step between the two bands.
        bottomPadding = CARD_OVERLAP + Spacing.xl,
    )


}

/**
 * Keeps the pager and the selected national code in step in both directions: a restored
 * selection scrolls the pager, and a swipe reports the new selection back.
 */
@Composable
private fun SyncPagerWithSelection(
    state: TreatmentUiState,
    patients: List<PatientItem>,
    pagerState: PagerState,
    onIntent: (TreatmentIntent) -> Unit,
) {
    LaunchedEffect(state.selectedNationalCode, patients) {
        val index = patients.indexOfFirst { it.nationalId == state.selectedNationalCode }
        if (index >= 0 && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage, patients) {
        val selected = patients.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
        if (selected.nationalId != state.selectedNationalCode) {
            onIntent(TreatmentIntent.SelectPatient(selected.nationalId, selected.fullName))
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
            TextButton(onClick = onDismiss) { Text("تایید") }
        },
        title = { Text("علت عدم استحقاق درمان") },
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
