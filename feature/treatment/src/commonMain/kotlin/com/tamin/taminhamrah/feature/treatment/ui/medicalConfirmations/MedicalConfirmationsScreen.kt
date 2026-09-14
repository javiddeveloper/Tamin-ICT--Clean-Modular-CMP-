package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.tamin.taminhamrah.feature.treatment.ui.components.ConfirmationSavedDialog
import com.tamin.taminhamrah.feature.treatment.ui.components.ConfirmationsList
import com.tamin.taminhamrah.feature.treatment.ui.components.MedicalConfirmationDetailView
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminHeroGradient
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.pushBack
import com.tamin.taminhamrah.ui.pushForward
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkSoft
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.category_approvals
import taminx.core.core_ui.confirmations_details_title
import taminx.core.core_ui.confirmations_hero_subtitle
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_medical_approvals

@Composable
fun MedicalConfirmationsRoute(
    viewModel: MedicalConfirmationsViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var savedToInboxVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ConfirmationsIntent.LoadList)
    }

    HandleConfirmationsEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onSavedToInbox = { savedToInboxVisible = true },
    )

    MedicalConfirmationsScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        snackbarHostState = snackbarHostState,
        savedToInboxVisible = savedToInboxVisible,
        onDismissSavedToInbox = { savedToInboxVisible = false },
    )
}

@Composable
fun HandleConfirmationsEvents(
    events: Flow<ConfirmationsEvent>,
    snackbarHostState: SnackbarHostState,
    onSavedToInbox: () -> Unit,
) {
    events.collectWithLifecycleAware {
        when (it) {
            is ConfirmationsEvent.ShowToast -> snackbarHostState.showSnackbar(getString(it.message))
            ConfirmationsEvent.SavedToInbox -> onSavedToInbox()
        }
    }
}

@Composable
fun MedicalConfirmationsScreen(
    state: ConfirmationsUiState,
    onIntent: (ConfirmationsIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    savedToInboxVisible: Boolean = false,
    onDismissSavedToInbox: () -> Unit = {},
) {
    val colors = LocalTaminColors.current
    var showingRepId by remember { mutableStateOf<String?>(null) }
    var selectedDetail by remember { mutableStateOf<MedicalConfirmationPR?>(null) }
    val collapseState = rememberCollapsingHeaderState(TreatmentConfirmationsDimens.headerCollapseDistance)

    // Both live above the swap, because both have to outlive it: the list is torn down while the
    // detail is on screen, so held inside it the chosen filter would silently reset to «همه» and
    // every row would fade in again underneath the slide back.
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val staggerState = rememberStaggeredEntranceState()

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(
                    if (selectedDetail != null) Res.string.confirmations_details_title
                    else Res.string.category_approvals
                ),
                background = taminHeroGradient(colors.topAppBarStops),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = {
                            if (selectedDetail != null) selectedDetail = null
                            else onBackClicked()
                        },
                    )
                },
                content = {
                    if (selectedDetail == null) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AnimatedRingHeaderIcon(
                                icon = vectorResource(Res.drawable.ic_tamin_medical_approvals)
                            )
                            Spacer(modifier = Modifier.padding(top = Spacing.xs))
                            Text(
                                text = stringResource(Res.string.confirmations_hero_subtitle),
                                style = typography.bodyMedium,
                                color = TaminOnAccentInkSoft,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Opening a row is a step deeper into the page and closing it is a step back out, so
            // it animates the way the app pushes a screen rather than dissolving in place.
            AnimatedContent(
                targetState = selectedDetail,
                transitionSpec = { if (targetState != null) pushForward() else pushBack() },
                label = "confirmations-screen-transition",
            ) { detailItem ->
                if (detailItem != null) {
                    MedicalConfirmationDetailView(
                        item = detailItem,
                        onOpenCertificate = { repId -> showingRepId = repId },
                        onSendToInbox = { repId -> onIntent(ConfirmationsIntent.SendToInbox(repId)) },
                    )
                } else {
                    ConfirmationsList(
                        confirmations = state.confirmationList,
                        isLoading = state.isLoading,
                        error = state.error,
                        selectedFilterIndex = selectedFilterIndex,
                        onFilterSelected = { selectedFilterIndex = it },
                        staggerState = staggerState,
                        onSelectDetail = { item -> selectedDetail = item },
                        modifier = Modifier.nestedScroll(collapseState.nestedScrollConnection),
                    )
                }
            }
        }
    }

    if (savedToInboxVisible) {
        ConfirmationSavedDialog(onDismiss = onDismissSavedToInbox)
    }

    showingRepId?.let { repId ->
        TaminPdfViewer(
            fileName = "medical_confirmation_$repId.pdf",
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = { onIntent(ConfirmationsIntent.DownloadPdf(repId)) },
            onDismiss = {
                showingRepId = null
                onIntent(ConfirmationsIntent.DismissPdfViewer)
            },
        )
    }
}

@PreviewRtlTheme
@Composable
fun PreviewMedicalConfirmationsScreen() {
    PreviewRtlThemeContent {
        MedicalConfirmationsScreen(
            state = TreatmentMocks.confirmationsUiState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
fun PreviewConfirmationSavedDialog() {
    PreviewRtlThemeContent {
        ConfirmationSavedDialog(onDismiss = {})
    }
}
