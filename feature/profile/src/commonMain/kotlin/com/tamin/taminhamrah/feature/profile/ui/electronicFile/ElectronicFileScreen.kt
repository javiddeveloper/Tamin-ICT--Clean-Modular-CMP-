package com.tamin.taminhamrah.feature.profile.ui.electronicFile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.components.DocumentGrid
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.components.ElectronicFileHeader
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileEvent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileIntent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.DocumentTarget
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.electronic_file_section
import taminx.core.core_ui.electronic_file_title
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun ElectronicFileRoute(
    viewModel: ElectronicFileViewModel,
    onBackClicked: () -> Unit,
) {
    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            ElectronicFileEvent.NavigateBack -> onBackClicked()
        }
    }

    val state by viewModel.uiState.collectAsState()
    val documents = viewModel.documents.collectAsLazyPagingItems()

    val onIntent = viewModel::sendIntent
    val onOpen = remember(onIntent) {
        { document: ElectronicFilePR ->
            onIntent(ElectronicFileIntent.OpenDocument(document))
        }
    }
    val onDismissViewer = remember(onIntent) { { onIntent(ElectronicFileIntent.DismissViewer) } }
    val onBack = remember(onIntent) { { onIntent(ElectronicFileIntent.OnBackClicked) } }

    val colors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        TaminTopAppBar(
            title = stringResource(Res.string.electronic_file_title),
            centerTitle = true,
            background = taminTopAppBarGradient(colors.profileGradientStops),
            cornerRadius = CornerRadius.sheet,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.action_back),
                    onClick = onBack,
                    bordered = true,
                )
            },
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))
            ElectronicFileHeader()
        }

        Text(
            text = stringResource(Res.string.electronic_file_section),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textTertiary,
            modifier = Modifier.padding(start = Spacing.page, end = Spacing.page, top = Spacing.md, bottom = Spacing.xs),
        )

        DocumentGrid(
            documents = documents,
            onOpen = onOpen,
            modifier = Modifier.fillMaxSize(),
        )
    }

    // A failed refresh leaves the grid with nothing to show, so closing the dialog leaves the
    // screen rather than a blank page.
    ErrorStateView(
        message = (documents.loadState.refresh as? LoadState.Error)?.error?.message,
        onDismiss = onBackClicked,
        onRetry = documents::retry,
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
