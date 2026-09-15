package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestUiState
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import com.tamin.taminhamrah.model.constructionInsurance.EnumTextColor
import com.tamin.taminhamrah.model.constructionInsurance.KeyValueModel
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.no_construction_files_found
import taminx.core.core_ui.view_detail_request_title

@Composable
fun ViewDetailRequestRoute(
    viewModel: ViewDetailRequestViewModel,
    fileNumber: Long?,
    requestNumber: Long?,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber, requestNumber))
    }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            ViewDetailRequestEvent.NavigateBack -> onBackClicked()
        }
    }

    ViewDetailRequestScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun ViewDetailRequestScreen(
    state: ViewDetailRequestUiState,
    onIntent: (ViewDetailRequestIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.view_detail_request_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(CoreRes.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            val file = state.file
            when {
                state.error != null && file == null -> ViewDetailErrorState(
                    message = state.error,
                    onRetry = { onIntent(ViewDetailRequestIntent.Retry) },
                )

                file == null && !state.isLoading -> TaminEmptyState(
                    message = stringResource(CoreRes.string.no_construction_files_found),
                    modifier = Modifier.padding(top = Spacing.xxl),
                )

                file != null -> ViewDetailContent(file = file)
            }

            if (state.isLoading) {
                LoadingStateOverlay()
            }
        }
    }
}

@Composable
private fun ViewDetailContent(file: ConstructionFilePR, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item { KeyValueSection(items = file.getDetailConstructionFile()) }
        item { KeyValueSection(items = file.getRequestInfo()) }
        item { KeyValueSection(items = file.getComputingInfo()) }
    }
}

@Composable
private fun KeyValueSection(items: List<KeyValueModel>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        items.forEach { kv ->
            val label = kv.keyResId?.let { stringResource(it) } ?: kv.keyString.orEmpty()
            val value = kv.valueResId?.let { stringResource(it) } ?: kv.value
            DetailRow(
                label = label,
                value = value,
                valueColor = colorFor(kv.textColor),
                // Many of these rows are free text (address, payment type) rather than a numeric
                // code, and NumericText forces single-line LTR — wrong for Persian prose.
                numeric = false,
            )
        }
    }
}

@Composable
private fun colorFor(textColor: EnumTextColor) = when (textColor) {
    EnumTextColor.DEFAULT -> LocalTaminColors.current.textPrimary
    EnumTextColor.AMBER -> LocalTaminColors.current.orangeText
    EnumTextColor.GREEN -> LocalTaminColors.current.greenText
    EnumTextColor.RED -> LocalTaminColors.current.dangerText
    EnumTextColor.BLUE -> LocalTaminColors.current.blueText
}

@Composable
private fun ViewDetailErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(text = message, color = LocalTaminColors.current.dangerText)
        TaminOutlinedButton(text = stringResource(CoreRes.string.action_retry), onClick = onRetry)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewFile = ConstructionFilePR(
    fileNumber = 4479890882L,
    requestNumber = 123456789L,
    requestDate = "14020901",
    workshopInfo = WorkshopIdInfoPR(workshopRegisterDate = "14020901", workshopId = "9028222442", brhCode = "6400"),
    postalCode = "9187955511",
    address = "مشهد - بلوار وکیل آباد",
    mainPlaque = 12,
    subPlaque = 3,
    block = 5L,
    propertyConstruction = 1400,
    apartment = 2,
    trade = 1,
    partPlaque = 4,
    debitNumber = "123456789012",
    totalPayment = 1_850_000L,
    meterage = 120,
    debitStatusCode = "51",
    paymentDeadLine = "14021001",
)

@PreviewRtlTheme
@Composable
private fun ViewDetailRequestScreenPreview() {
    PreviewRtlThemeContent {
        ViewDetailRequestScreen(
            state = ViewDetailRequestUiState(items = kotlinx.collections.immutable.persistentListOf(PreviewFile)),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ViewDetailRequestScreenLoadingPreview() {
    PreviewRtlThemeContent {
        ViewDetailRequestScreen(
            state = ViewDetailRequestUiState(isLoading = true),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
