package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterUiState
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
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
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.deferred_installment_rial
import taminx.core.core_ui.installment_letter_empty
import taminx.core.core_ui.installment_letter_title
import taminx.core.core_ui.label_debit_end_date
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_debit_start_date
import taminx.core.core_ui.label_remaining_amount
import taminx.core.core_ui.Res as CoreRes

/** مرحله بدهی status text this branch's data uses; matched by substring since no status code exists on [InstallmentLetterPR]. */
private const val STATUS_KEYWORD_OVERDUE = "معوق"
private const val STATUS_KEYWORD_PAID = "پرداخت شده"

@Composable
fun InstallmentLetterRoute(
    viewModel: InstallmentLetterViewModel,
    workshopId: String,
    branchId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId, branchId))
    }

    InstallmentLetterEvents(events = viewModel.events, toaster = toaster, onBackClicked = onBackClicked)

    InstallmentLetterScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
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
                        InstallmentLetterCard(item = letter)
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

@Composable
private fun InstallmentLetterCard(item: InstallmentLetterPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (statusContainer, statusContent) = installmentStatusColors(item.debitStatusDescription)

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

        TaminDivider()

        DetailRow(
            label = stringResource(CoreRes.string.label_debit_number),
            value = item.debitNumber.orDash(),
        )
        DetailRow(
            label = stringResource(CoreRes.string.label_debit_start_date),
            value = item.debitStartDate?.toFormattedDate().orDash(),
        )
        DetailRow(
            label = stringResource(CoreRes.string.label_debit_end_date),
            value = item.debitEndDate?.toFormattedDate().orDash(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(statusContainer, RoundedCornerShape(CornerRadius.md))
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(CoreRes.string.label_remaining_amount),
                style = MaterialTheme.typography.bodySmall,
                color = statusContent,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                NumericText(
                    text = (item.remainingAmount ?: 0L).toPriceFormat(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = statusContent,
                )
                Text(
                    text = stringResource(CoreRes.string.deferred_installment_rial),
                    style = MaterialTheme.typography.bodySmall,
                    color = statusContent
                )
            }
        }
    }
}

/**
 * Maps the free-text `debitStatusDescription` to a status-pill color pair. There is no status
 * code on [InstallmentLetterPR] (see `InstallmentLetterDN`/`InstallmentLetterDTO`) — only the
 * description string the API sends — so this matches the known phrases substring-wise and falls
 * back to a neutral blue for anything else (e.g. «سررسید نشده») rather than guessing.
 */
@Composable
private fun installmentStatusColors(status: String?): Pair<Color, Color> {
    val colors = LocalTaminColors.current
    return when {
        status == null -> colors.bgPage to colors.textMuted
        status.contains(STATUS_KEYWORD_OVERDUE) -> colors.dangerBorder to colors.dangerText
        status.contains(STATUS_KEYWORD_PAID) -> colors.greenBg to colors.greenText
        else -> colors.blueBg to colors.blueText
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewLetters = kotlinx.collections.immutable.persistentListOf(
    InstallmentLetterPR(
        workshopId = "9028222442",
        debitNumber = "7764000001",
        debitStepDescription = "قسط اول",
        debitStatusDescription = "پرداخت شده",
        debitStartDate = "14021001",
        debitEndDate = "14031001",
        remainingAmount = 2_000_000L,
    ),
    InstallmentLetterPR(
        workshopId = "9028222442",
        debitNumber = "7764000002",
        debitStepDescription = "قسط دوم",
        debitStatusDescription = "سررسید نشده",
        debitStartDate = "14031001",
        debitEndDate = "14041001",
        remainingAmount = 1_600_000L,
    ),
    InstallmentLetterPR(
        workshopId = "9028222442",
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
            state = InstallmentLetterUiState(items = PreviewLetters),
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

