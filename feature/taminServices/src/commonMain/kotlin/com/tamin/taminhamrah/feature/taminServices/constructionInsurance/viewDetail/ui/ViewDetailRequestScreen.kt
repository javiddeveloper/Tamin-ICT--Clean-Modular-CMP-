package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui.components.ViewDetailRequestSkeleton
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import com.tamin.taminhamrah.model.constructionInsurance.EnumTextColor
import com.tamin.taminhamrah.model.constructionInsurance.KeyValueModel
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.no_construction_files_found
import taminx.core.core_ui.view_detail_request_title
import taminx.core.core_ui.view_detail_section_computing_info
import taminx.core.core_ui.view_detail_section_file_info
import taminx.core.core_ui.view_detail_section_request_info
import taminx.core.core_ui.Res as CoreRes

@Composable
fun ViewDetailRequestRoute(
    viewModel: ViewDetailRequestViewModel,
    fileNumber: Long?,
    requestNumber: Long?,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber, requestNumber))
    }

    ViewDetailRequestEvents(events = viewModel.events, toaster = toaster, onBackClicked = onBackClicked)

    ViewDetailRequestScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun ViewDetailRequestEvents(
    events: Flow<ViewDetailRequestEvent>,
    toaster: ToasterState,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ViewDetailRequestEvent.NavigateBack -> onBackClicked()
            is ViewDetailRequestEvent.ShowError -> toaster.error(event.message)
        }
    }
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

                file == null && state.isLoading -> ViewDetailRequestSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.page, vertical = Spacing.md),
                )

                file == null && !state.isLoading -> EmptyStateMessage(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = stringResource(CoreRes.string.no_construction_files_found),
                    showIconTile = true,
                    modifier = Modifier.align(Alignment.Center),
                )

                file != null -> ViewDetailContent(file = file)
            }

            if (state.isLoading && file != null) {
                LoadingStateOverlay()
            }
        }
    }
}

private val SectionShadowBlur = 26.dp
private val SectionShadowOffsetY = 10.dp

@Composable
private fun ViewDetailContent(file: ConstructionFilePR, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            ExpandableDetailCard(
                title = stringResource(CoreRes.string.view_detail_section_file_info),
                items = file.getDetailConstructionFile(),
            )
        }
        item {
            ExpandableDetailCard(
                title = stringResource(CoreRes.string.view_detail_section_request_info),
                items = file.getRequestInfo(),
            )
        }
        item {
            ExpandableDetailCard(
                title = stringResource(CoreRes.string.view_detail_section_computing_info),
                items = file.getComputingInfo(),
            )
        }
    }
}

@Composable
private fun ExpandableDetailCard(
    title: String,
    items: List<KeyValueModel>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var expanded by rememberSaveable(title) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) -90f else 90f,
        label = "view-detail-section-chevron",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = SectionShadowBlur,
                offsetY = SectionShadowOffsetY,
            )
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Icon(
                imageVector = vectorResource(CoreRes.drawable.ic_tamin_chevron_back),
                contentDescription = stringResource(
                    if (expanded) CoreRes.string.action_hide_details else CoreRes.string.action_show_details,
                ),
                tint = colors.blueText,
                modifier = Modifier
                    .size(Spacing.lg)
                    .graphicsLayer { rotationZ = rotation },
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                items.forEach { kv ->
                    val label = kv.keyResId?.let { stringResource(it) } ?: kv.keyString.orEmpty()
                    val value = kv.valueResId?.let { stringResource(it) } ?: kv.value
                    DetailRow(
                        label = label,
                        value = value,
                        valueColor = colorFor(kv.textColor),
                        numeric = kv.numeric,
                        unit = kv.unit,
                    )
                }
            }
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

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewFile = ConstructionFilePR(
    fileNumber = 4479890882L,
    requestNumber = 123456789L,
    requestDate = "14020901",
    workshopInfo = WorkshopIdInfoPR(
        workshopRegisterDate = "14020901",
        workshopId = "9028222442",
        brhCode = "6400"
    ),
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
            state = ViewDetailRequestUiState(
                items = kotlinx.collections.immutable.persistentListOf(
                    PreviewFile
                )
            ),
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

@PreviewRtlTheme
@Composable
private fun ViewDetailRequestScreenEmptyPreview() {
    PreviewRtlThemeContent {
        ViewDetailRequestScreen(
            state = ViewDetailRequestUiState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
