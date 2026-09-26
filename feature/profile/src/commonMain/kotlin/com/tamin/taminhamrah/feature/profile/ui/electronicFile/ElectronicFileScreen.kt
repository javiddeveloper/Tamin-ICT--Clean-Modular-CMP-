package com.tamin.taminhamrah.feature.profile.ui.electronicFile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.components.DocumentGrid
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.components.ElectronicFileHeader
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileEvent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileIntent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileUiState
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.DocumentTarget
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import kotlinx.coroutines.flow.Flow

@Composable
fun ElectronicFileRoute(
    viewModel: ElectronicFileViewModel,
    onBackClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.sendIntent(ElectronicFileIntent.LoadNationalCode)
        viewModel.sendIntent(ElectronicFileIntent.LoadDocuments)
    }

    HandleElectronicFileEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
    )

    val state by viewModel.uiState.collectAsState()

    ElectronicFileScreen(
        state = state,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
fun HandleElectronicFileEvents(
    events: Flow<ElectronicFileEvent>,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ElectronicFileEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
fun ElectronicFileScreen(
    state: ElectronicFileUiState,
    onIntent: (ElectronicFileIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val onOpen = remember(onIntent) {
        { document: ElectronicFilePR ->
            onIntent(ElectronicFileIntent.OpenDocument(document))
        }
    }
    val onDismissViewer = remember(onIntent) { { onIntent(ElectronicFileIntent.DismissViewer) } }
    val onBack = remember(onIntent) { { onIntent(ElectronicFileIntent.OnBackClicked) } }
    val onRetry = remember(onIntent) { { onIntent(ElectronicFileIntent.LoadDocuments) } }

    val colors = LocalTaminColors.current
    val topArea = rememberMeasuredTopAreaState { probeState ->
        ElectronicFileHeader(
            topAreaState = probeState,
            onBackClicked = {},
        )
    }
    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        DocumentGrid(
            documents = state.documents,
            isLoading = state.isLoading,
            hasError = state.errorMessage != null,
            onOpen = onOpen,
            gridState = gridState,
            modifier = Modifier
                .fillMaxSize()
                .driveTopArea(topArea, gridState),
            contentPadding = topAreaContentPadding(
                state = topArea,
                rest = PaddingValues(
                    start = Spacing.page,
                    end = Spacing.page,
                    bottom = Spacing.page,
                ),
            ),
        )

        ElectronicFileHeader(
            topAreaState = topArea,
            onBackClicked = onBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )
    }

    // A failed refresh leaves the grid with nothing to show, so closing the dialog leaves the
    // screen rather than a blank page.
    ErrorStateView(
        message = state.errorMessage,
        onDismiss = onBackClicked,
        onRetry = onRetry,
    )

    when (val target = state.openTarget) {
        is DocumentTarget.Image -> TaminImageViewer(
            title = target.title,
            url = target.url,
            background = taminTopAppBarGradient(colors.profileGradientStops),
            onDismiss = onDismissViewer,
        )

        is DocumentTarget.Pdf -> {
            val pdfFileName = remember(target.fileName, state.nationalCode) {
                buildString {
                    append(target.fileName)
                    if (state.nationalCode.isNotBlank()) {
                        append('_')
                        append(state.nationalCode)
                    }
                    append(".pdf")
                }
            }
            TaminPdfViewer(
                fileName = pdfFileName,
                title = target.fileName,
                background = taminTopAppBarGradient(colors.profileGradientStops),
                pdf = state.pdfPR,
                downloadFailed = state.downloadFailed,
                onRequestDownload = { onIntent(ElectronicFileIntent.DownloadPdf(target.url)) },
                onDismiss = onDismissViewer,
            )
        }

        null -> Unit
    }
}
