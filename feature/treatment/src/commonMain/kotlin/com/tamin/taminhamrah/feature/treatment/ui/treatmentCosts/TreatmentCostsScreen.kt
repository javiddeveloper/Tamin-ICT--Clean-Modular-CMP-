package com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentCostsDimens
import com.tamin.taminhamrah.feature.treatment.ui.components.CertificateList
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.collapseHeightAway
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminHeroGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.category_misc_claims
import taminx.core.core_ui.costs_hero_subtitle
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_misc_claims
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkSoft


@Composable
fun TreatmentCostsRoute(
    viewModel: TreatmentCostsViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(CostsIntent.LoadList)
    }

    HandleTreatmentCostsEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
    )


    TreatmentCostsScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun HandleTreatmentCostsEvents(
    events: Flow<CostsEvent>,
    snackbarHostState: SnackbarHostState,
) {
    events.collectWithLifecycleAware {
        when (it) {
            is CostsEvent.ShowToast -> snackbarHostState.showSnackbar(getString(it.message))
        }
    }
}

@Composable
fun TreatmentCostsScreen(
    state: CostsUiState,
    onIntent: (CostsIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = LocalTaminColors.current

    // Which certificate is on screen. Opening the viewer no longer means a download has happened:
    // it decides for itself whether the file still needs fetching, so the tap only says which one.
    var showingRepId by remember { mutableStateOf<String?>(null) }

    val collapseState = rememberCollapsingHeaderState(TreatmentCostsDimens.headerCollapseDistance)

    val certificates = remember(state.treatmentCostList) {
        state.treatmentCostList.toImmutableList()
    }

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.category_misc_claims),
                background = taminHeroGradient(colors.treatmentHubStops),
                navigationIcon = {
                    TaminTopAppBarButton(
                        bordered = true,
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBackClicked,
                    )
                },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Folds away as the list scrolls, leaving only the title row.
                            .collapseHeightAway(collapseState.progressProvider)
                            .padding(top = Spacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(
                            icon = vectorResource(Res.drawable.ic_tamin_misc_claims)
                        )
                        Spacer(modifier = Modifier.padding(top = Spacing.xs))
                        Text(
                            text = stringResource(Res.string.costs_hero_subtitle),
                            style = typography.bodyMedium,
                            color = TaminOnAccentInkSoft,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(collapseState.nestedScrollConnection),
        ) {
            CertificateList(
                certificates = certificates,
                isLoading = state.isLoading,
                error = state.error,
                onOpenCertificate = { repId -> showingRepId = repId },
                onSendToInbox = { repId -> onIntent(CostsIntent.SendToInbox(repId)) },
            )
        }
    }

    ErrorStateView(
        message = state.error,
        onDismiss = onBackClicked,
        onRetry = { onIntent(CostsIntent.LoadList) },
    )

    showingRepId?.let { repId ->
        TaminPdfViewer(
            background = taminHeroGradient(colors.treatmentHubStops),
            // Named after the certificate, so one already downloaded is recognized and re-rendered
            // from the device instead of being fetched and saved twice.
            fileName = "treatment_cost_$repId.pdf",
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = { onIntent(CostsIntent.DownloadPdf(repId)) },
            onDismiss = {
                showingRepId = null
                onIntent(CostsIntent.DismissPdfViewer)
            },
        )
    }
}

@PreviewRtlTheme
@Composable
fun PreviewTreatmentCostsScreen() {
    PreviewRtlThemeContent {
        TreatmentCostsScreen(
            state = TreatmentMocks.costsUiState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}
