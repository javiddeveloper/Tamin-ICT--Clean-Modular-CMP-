package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.treatment.ui.components.ConfirmationSavedDialog
import com.tamin.taminhamrah.feature.treatment.ui.components.MedicalConfirmationDetailView
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminHeroGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.confirmations_details_title
import taminx.core.core_ui.ic_tamin_chevron_back

/**
 * One confirmation, as its own destination.
 *
 * The row is found in the list the shared [MedicalConfirmationsViewModel] already holds rather than
 * carried through the route: the service sends no row identifier, so [MedicalConfirmationPR.listKey]
 * -- the same key the list is keyed on -- is what the route passes. A key that matches nothing (the
 * list was never loaded, e.g. after process death) pops straight back to it instead of drawing an
 * empty page.
 */
@Composable
fun MedicalConfirmationDetailRoute(
    viewModel: MedicalConfirmationsViewModel,
    listKey: String,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var savedToInboxVisible by remember { mutableStateOf(false) }

    val item = remember(uiState.confirmationList, listKey) {
        uiState.confirmationList.firstOrNull { it.listKey == listKey }
    }

    HandleConfirmationsEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onSavedToInbox = { savedToInboxVisible = true },
    )

    // Nothing to show: the list this row came from is gone, so go back to it rather than sit empty.
    LaunchedEffect(item, uiState.isLoading) {
        if (item == null && !uiState.isLoading) onBackClicked()
    }

    item?.let {
        MedicalConfirmationDetailScreen(
            item = it,
            state = uiState,
            onIntent = viewModel::sendIntent,
            onBackClicked = onBackClicked,
            snackbarHostState = snackbarHostState,
            savedToInboxVisible = savedToInboxVisible,
            onDismissSavedToInbox = { savedToInboxVisible = false },
        )
    }
}

@Composable
fun MedicalConfirmationDetailScreen(
    item: MedicalConfirmationPR,
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

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.confirmations_details_title),
                background = taminHeroGradient(colors.treatmentHubStops),
                navigationIcon = {
                    TaminTopAppBarButton(
                        bordered = true,
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBackClicked,
                    )
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            MedicalConfirmationDetailView(
                item = item,
                onOpenCertificate = { repId -> showingRepId = repId },
                onSendToInbox = { repId -> onIntent(ConfirmationsIntent.SendToInbox(repId)) },
            )
        }
    }

    if (savedToInboxVisible) {
        ConfirmationSavedDialog(onDismiss = onDismissSavedToInbox)
    }

    showingRepId?.let { repId ->
        TaminPdfViewer(
            background = taminHeroGradient(colors.treatmentHubStops),
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
private fun PreviewMedicalConfirmationDetailScreen() {
    PreviewRtlThemeContent {
        MedicalConfirmationDetailScreen(
            item = TreatmentMocks.confirmationsUiState.confirmationList.first(),
            state = TreatmentMocks.confirmationsUiState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}
