package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui.components.InstallmentLetterAction
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui.components.installmentLetterActions
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionRequestHeroCard
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.RecordActionMenu
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.paging.OnLoadMore
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.btn_action
import taminx.core.core_ui.btn_installment_debit_list
import taminx.core.core_ui.deferred_installment_rial
import taminx.core.core_ui.from_date
import taminx.core.core_ui.ic_calculator
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.installment_letter_empty
import taminx.core.core_ui.installment_letter_title
import taminx.core.core_ui.label_debit_end_date
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_debit_start_date
import taminx.core.core_ui.label_remaining_amount
import taminx.core.core_ui.to_date
import taminx.core.core_ui.workshop_number
import taminx.core.core_ui.Res as CoreRes

@Composable
fun InstallmentLetterRoute(
    viewModel: InstallmentLetterViewModel,
    fileNumber: Long?,
    workshopId: String,
    branchId: String,
    onBackClicked: () -> Unit,
    onNavigateToInstallmentManagement: (
        fileNumber: Long?,
        workshopId: String?,
        branchId: String,
        debitNumber: String,
        debitStepDescription: String?,
    ) -> Unit = { _, _, _, _, _ -> },
    onNavigateToInstallmentDebitList: (
        fileNumber: Long?,
        workshopId: String?,
        branchId: String,
        debitNumber: String,
    ) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(InstallmentLetterIntent.Load(fileNumber, workshopId, branchId))
    }

    InstallmentLetterEvents(events = viewModel.events, toaster = toaster, onBackClicked = onBackClicked)

    InstallmentLetterScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        onNavigateToInstallmentManagement = onNavigateToInstallmentManagement,
        onNavigateToInstallmentDebitList = onNavigateToInstallmentDebitList,
        modifier = modifier,
    )
}

@Composable
fun InstallmentLetterEvents(
    events: Flow<InstallmentLetterEvent>,
    toaster: ToasterState,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            InstallmentLetterEvent.NavigateBack -> onBackClicked()
            is InstallmentLetterEvent.ShowError -> toaster.error(event.message)
        }
    }
}

@Composable
fun InstallmentLetterScreen(
    state: InstallmentLetterUiState,
    onIntent: (InstallmentLetterIntent) -> Unit,
    onBackClicked: () -> Unit,
    onNavigateToInstallmentManagement: (
        fileNumber: Long?,
        workshopId: String?,
        branchId: String,
        debitNumber: String,
        debitStepDescription: String?,
    ) -> Unit = { _, _, _, _, _ -> },
    onNavigateToInstallmentDebitList: (
        fileNumber: Long?,
        workshopId: String?,
        branchId: String,
        debitNumber: String,
    ) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val listState = rememberLazyListState()

    listState.OnLoadMore(
        enabled = !state.endReached && state.paginationError == null,
    ) {
        onIntent(InstallmentLetterIntent.LoadNextPage)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.installment_letter_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(CoreRes.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
                content = {
                    ConstructionRequestHeroCard(
                        fileNumber = state.fileNumber,
                        workshopId = state.workshopId,
                        branchCode = state.branchId,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.md),
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.isLoading && state.items.isEmpty() -> InstallmentLetterSkeleton(
                    modifier = Modifier.fillMaxSize(),
                )

                state.items.isEmpty() && state.paginationError != null -> PagingFooter(
                    isLoadingNextPage = false,
                    error = state.paginationError,
                    onRetry = { onIntent(InstallmentLetterIntent.RetryNextPage) },
                    modifier = Modifier.align(Alignment.Center),
                )

                state.items.isEmpty() -> EmptyStateMessage(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = stringResource(CoreRes.string.installment_letter_empty),
                    showIconTile = true,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = Spacing.page,
                        vertical = Spacing.md
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    items(
                        items = state.items,
                        key = { it.debitNumber ?: it.hashCode() }) { letter ->
                        InstallmentLetterCard(
                            item = letter,
                            onActionSelect = { action ->
                                val debitNumber = letter.debitNumber.orEmpty()
                                when (action) {
                                    InstallmentLetterAction.ManagementAndPaymentSheet ->
                                        onNavigateToInstallmentManagement(
                                            state.fileNumber,
                                            letter.workshopId,
                                            state.branchId,
                                            debitNumber,
                                            letter.debitStepDescription,
                                        )

                                    InstallmentLetterAction.DebitList ->
                                        onNavigateToInstallmentDebitList(
                                            state.fileNumber,
                                            letter.workshopId,
                                            state.branchId,
                                            debitNumber,
                                        )
                                }
                            },
                            onDebitListShortcutClick = {
                                onNavigateToInstallmentDebitList(
                                    state.fileNumber,
                                    letter.workshopId,
                                    state.branchId,
                                    letter.debitNumber.orEmpty(),
                                )
                            },
                        )
                    }
                    item {
                        PagingFooter(
                            isLoadingNextPage = state.isLoadingNextPage,
                            error = state.paginationError,
                            onRetry = { onIntent(InstallmentLetterIntent.RetryNextPage) },
                        )
                    }
                }
            }
        }
    }
}

private val ButtonHeight = 44.dp

@Composable
private fun InstallmentLetterCard(
    item: InstallmentLetterPR,
    modifier: Modifier = Modifier,
    onActionSelect: (InstallmentLetterAction) -> Unit = {},
    onDebitListShortcutClick: () -> Unit = {},
) {
    val colors = LocalTaminColors.current
    val (statusContainer, statusContent) = installmentStatusColors()
    var menuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp
            )
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.debitStepDescription.orDash(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            item.debitStatusDescription?.let { status ->
                StatusPill(
                    text = status,
                    containerColor = statusContainer,
                    contentColor = statusContent
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            InfoTile(
                label = stringResource(CoreRes.string.label_debit_number),
                value = item.debitNumber.orDash(),
                modifier = Modifier.weight(1f),
            )
            InfoTile(
                label = stringResource(CoreRes.string.workshop_number),
                value = item.workshopId.orDash(),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(10.dp),
                painter = painterResource(CoreRes.drawable.ic_tamin_calendar),
                contentDescription = ""
            )
            Text(
                text = stringResource(CoreRes.string.from_date),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            NumericText(
                text = item.debitStartDate?.toFormattedDate().orDash(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(CoreRes.string.to_date),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            NumericText(
                text = item.debitEndDate?.toFormattedDate().orDash(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textPrimary,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminOutlinedButton(
                text = stringResource(CoreRes.string.btn_installment_debit_list),
                onClick = onDebitListShortcutClick,
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                iconPosition = IconPosition.Start,
                height = ButtonHeight,
                textStyle = MaterialTheme.typography.labelMedium,
                containerColor = colors.bgPage,
                borderColor = Color.Transparent,
                contentColor = colors.blueText,
                modifier = Modifier.weight(2f),
            )

            Box(modifier = Modifier.weight(1f)) {
                TaminFilledButton(
                    text = stringResource(CoreRes.string.btn_action),
                    onClick = { menuOpen = true },
                    icon = Icons.Default.Settings,
                    height = ButtonHeight,
                    textStyle = MaterialTheme.typography.labelMedium,
                    background = Brush.linearGradient(listOf(TaminNavy300, TaminNavy900)),
                    modifier = Modifier.fillMaxWidth(),
                )
                RecordActionMenu(
                    expanded = menuOpen,
                    items = installmentLetterActions(),
                    onDismiss = { menuOpen = false },
                    onSelect = { action ->
                        menuOpen = false
                        onActionSelect(action)
                    },
                )
            }
        }
    }
}

@Composable
private fun InfoTile(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        NumericText(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
    }
}

/**
 * The status-pill color pair for `debitStatusDescription`. There is no status code on
 * [InstallmentLetterPR] (see `InstallmentLetterDN`/`InstallmentLetterDTO`) — only free text the API
 * sends — so this doesn't try to classify it by matching Persian phrases (MR !244 review item 9);
 * every status gets the same neutral treatment, and the text itself is shown as-is on the pill.
 */
@Composable
private fun installmentStatusColors(): Pair<Color, Color> {
    val colors = LocalTaminColors.current
    return colors.blueBg to colors.blueText
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewLetters = persistentListOf(
    InstallmentLetterPR(
        workshopId = "2361847",
        debitNumber = "7764000001",
        debitStepDescription = "مرحلهٔ اول تقسیط بدهی ساختمانی",
        debitStatusDescription = "پرداخت شده",
        debitStartDate = "14030201",
        debitEndDate = "14040131",
        remainingAmount = 2_000_000L,
    ),
    InstallmentLetterPR(
        workshopId = "2361847",
        debitNumber = "7764000002",
        debitStepDescription = "مرحلهٔ دوم تقسیط جرایم مادهٔ ۱۰۰",
        debitStatusDescription = "سررسید نشده",
        debitStartDate = "14030801",
        debitEndDate = "14040730",
        remainingAmount = 1_600_000L,
    ),
    InstallmentLetterPR(
        workshopId = "2361847",
        debitNumber = "7764000003",
        debitStepDescription = "قسط سوم",
        debitStatusDescription = "معوق",
        debitStartDate = "14021001",
        debitEndDate = "14031001",
        remainingAmount = 4_250_000L,
    ),
)

@PreviewRtlTheme
@Composable
private fun InstallmentLetterScreenPreview() {
    PreviewRtlThemeContent {
        InstallmentLetterScreen(
            state = InstallmentLetterUiState(fileNumber = 123804L, items = PreviewLetters),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InstallmentLetterScreenEmptyPreview() {
    PreviewRtlThemeContent {
        InstallmentLetterScreen(
            state = InstallmentLetterUiState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InstallmentLetterScreenLoadingPreview() {
    PreviewRtlThemeContent {
        InstallmentLetterScreen(
            state = InstallmentLetterUiState(isLoading = true),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
