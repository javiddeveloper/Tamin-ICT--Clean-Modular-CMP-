package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.ui

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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
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
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract.InstallmentDebitListEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract.InstallmentDebitListIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract.InstallmentDebitListUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionRequestHeroCard
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
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
import com.tamin.taminhamrah.util.toFormattedDate
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.installment_debit_list_empty
import taminx.core.core_ui.installment_debit_list_title
import taminx.core.core_ui.label_debit_end_date
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_debit_start_date
import taminx.core.core_ui.workshop_number
import taminx.core.core_ui.Res as CoreRes

/** مرحله بدهی status text this branch's data uses; matched by substring, same idiom as `InstallmentLetterScreen`'s `installmentStatusColors`. */
private const val STATUS_KEYWORD_OVERDUE = "معوق"
private const val STATUS_KEYWORD_SETTLED = "تسویه"

@Composable
fun InstallmentDebitListRoute(
    viewModel: InstallmentDebitListViewModel,
    fileNumber: Long?,
    workshopId: String?,
    branchId: String,
    debitNumber: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(InstallmentDebitListIntent.Load(fileNumber, workshopId, branchId, debitNumber))
    }

    InstallmentDebitListEvents(events = viewModel.events, toaster = toaster, onBackClicked = onBackClicked)

    InstallmentDebitListScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun InstallmentDebitListEvents(
    events: Flow<InstallmentDebitListEvent>,
    toaster: ToasterState,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            InstallmentDebitListEvent.NavigateBack -> onBackClicked()
            is InstallmentDebitListEvent.ShowError -> toaster.error(event.message)
        }
    }
}

@Composable
fun InstallmentDebitListScreen(
    state: InstallmentDebitListUiState,
    onIntent: (InstallmentDebitListIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val listState = rememberLazyListState()

    listState.OnLoadMore(
        enabled = !state.endReached && state.paginationError == null,
    ) {
        onIntent(InstallmentDebitListIntent.LoadNextPage)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.installment_debit_list_title),
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
                state.isLoading && state.items.isEmpty() -> InstallmentDebitListSkeleton(
                    modifier = Modifier.fillMaxSize(),
                )

                state.items.isEmpty() && state.paginationError != null -> PagingFooter(
                    isLoadingNextPage = false,
                    error = state.paginationError,
                    onRetry = { onIntent(InstallmentDebitListIntent.RetryNextPage) },
                    modifier = Modifier.align(Alignment.Center),
                )

                state.items.isEmpty() -> EmptyStateMessage(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = stringResource(CoreRes.string.installment_debit_list_empty),
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
                        key = { "${it.debitNumber}-${it.debitStepDescription}" }
                    ) { debit ->
                        InstallmentDebitCard(item = debit)
                    }
                    item {
                        PagingFooter(
                            isLoadingNextPage = state.isLoadingNextPage,
                            error = state.paginationError,
                            onRetry = { onIntent(InstallmentDebitListIntent.RetryNextPage) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * One مرحله بدهی card — title + status pill, two-box شماره بدهی/شماره کارگاه fields, date range.
 * Mirrors `InstallmentLetterScreen`'s `InstallmentLetterCard` (identical field shape on
 * [InstallmentDebitListPR]/`InstallmentLetterPR`), minus the remaining-amount bar and عملیات
 * footer this screen's Figma design doesn't carry.
 */
@Composable
private fun InstallmentDebitCard(item: InstallmentDebitListPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (statusContainer, statusContent) = installmentDebitStatusColors(item.debitStatusDescription)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp,
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
            item.debitStatusDescription?.let { status ->
                StatusPill(
                    text = status,
                    containerColor = statusContainer,
                    contentColor = statusContent,
                )
            }
            Text(
                text = item.debitStepDescription.orDash(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            DebitInfoTile(
                label = stringResource(CoreRes.string.label_debit_number),
                value = item.debitNumber.orDash(),
                modifier = Modifier.weight(1f),
            )
            DebitInfoTile(
                label = stringResource(CoreRes.string.workshop_number),
                value = item.workshopId.orDash(),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = stringResource(CoreRes.string.label_debit_start_date),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            NumericText(
                text = item.debitStartDate?.toFormattedDate().orDash(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(CoreRes.string.label_debit_end_date),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            NumericText(
                text = item.debitEndDate?.toFormattedDate().orDash(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textPrimary,
            )
        }
    }
}

@Composable
private fun DebitInfoTile(label: String, value: String, modifier: Modifier = Modifier) {
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
 * Maps the free-text `debitStatusDescription` to a status-pill color pair, same substring idiom as
 * `InstallmentLetterScreen.installmentStatusColors` — there is no status code on
 * [InstallmentDebitListPR], only the description string the API sends.
 */
@Composable
private fun installmentDebitStatusColors(status: String?): Pair<Color, Color> {
    val colors = LocalTaminColors.current
    return when {
        status == null -> colors.bgPage to colors.textMuted
        status.contains(STATUS_KEYWORD_OVERDUE) -> colors.dangerBorder to colors.dangerText
        status.contains(STATUS_KEYWORD_SETTLED) -> colors.greenBg to colors.greenText
        else -> colors.blueBg to colors.blueText
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewDebits = kotlinx.collections.immutable.persistentListOf(
    InstallmentDebitListPR(
        workshopId = "2361847",
        debitNumber = "1402/64118",
        debitStepDescription = "قسط اول تا سوم",
        debitStatusDescription = "تسویه شده",
        debitStartDate = "14030201",
        debitEndDate = "14030631",
        remainingAmount = 0L,
    ),
    InstallmentDebitListPR(
        workshopId = "2361847",
        debitNumber = "1402/64118",
        debitStepDescription = "قسط چهارم تا ششم",
        debitStatusDescription = "در جریان",
        debitStartDate = "14030701",
        debitEndDate = "14040131",
        remainingAmount = 4_250_000L,
    ),
)

@PreviewRtlTheme
@Composable
private fun InstallmentDebitListScreenPreview() {
    PreviewRtlThemeContent {
        InstallmentDebitListScreen(
            state = InstallmentDebitListUiState(
                fileNumber = 123804L,
                workshopId = "2361847",
                branchId = "7",
                items = PreviewDebits,
            ),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InstallmentDebitListScreenEmptyPreview() {
    PreviewRtlThemeContent {
        InstallmentDebitListScreen(
            state = InstallmentDebitListUiState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
