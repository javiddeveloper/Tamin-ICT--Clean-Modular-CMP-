package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionRequestHeroCard
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
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
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.deferred_installment_rial
import taminx.core.core_ui.installment_management_empty
import taminx.core.core_ui.installment_management_title
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_installment_paid_count
import taminx.core.core_ui.label_payment_date
import taminx.core.core_ui.Res as CoreRes

@Composable
fun InstallmentManagementRoute(
    viewModel: InstallmentManagementViewModel,
    fileNumber: Long?,
    workshopId: String?,
    branchId: String,
    debitNumber: String,
    debitStepDescription: String? = null,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(
            InstallmentManagementIntent.Load(fileNumber, workshopId, branchId, debitNumber, debitStepDescription)
        )
    }

    InstallmentManagementEvents(events = viewModel.events, toaster = toaster, onBackClicked = onBackClicked)

    InstallmentManagementScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun InstallmentManagementEvents(
    events: Flow<InstallmentManagementEvent>,
    toaster: ToasterState,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            InstallmentManagementEvent.NavigateBack -> onBackClicked()
            is InstallmentManagementEvent.ShowError -> toaster.error(event.message)
        }
    }
}

@Composable
fun InstallmentManagementScreen(
    state: InstallmentManagementUiState,
    onIntent: (InstallmentManagementIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val listState = rememberLazyListState()

    listState.OnLoadMore(
        enabled = !state.endReached && state.paginationError == null,
    ) {
        onIntent(InstallmentManagementIntent.LoadNextPage)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.installment_management_title),
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
                state.isLoading && state.items.isEmpty() -> InstallmentManagementSkeleton(
                    modifier = Modifier.fillMaxSize(),
                )

                state.items.isEmpty() && state.paginationError != null -> PagingFooter(
                    isLoadingNextPage = false,
                    error = state.paginationError,
                    onRetry = { onIntent(InstallmentManagementIntent.RetryNextPage) },
                    modifier = Modifier.align(Alignment.Center),
                )

                state.items.isEmpty() -> EmptyStateMessage(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = stringResource(CoreRes.string.installment_management_empty),
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
                    item {
                        InstallmentSummaryCard(
                            debitStepDescription = state.debitStepDescription,
                            debitNumber = state.debitNumber,
                            paidCount = state.items.count { it.paymentDate != null },
                            totalCount = state.items.size,
                        )
                    }
                    items(
                        items = state.items,
                        key = { "${it.debitNumber}-${it.debitSubCode}" }
                    ) { installment ->
                        InstallmentCard(item = installment)
                    }
                    item {
                        PagingFooter(
                            isLoadingNextPage = state.isLoadingNextPage,
                            error = state.paginationError,
                            onRetry = { onIntent(InstallmentManagementIntent.RetryNextPage) },
                        )
                    }
                }
            }
        }
    }
}

/** Summary card seeded from the تقسیط‌نامه row this screen was opened from — title + paid-count/debit-number rows. */
@Composable
private fun InstallmentSummaryCard(
    debitStepDescription: String?,
    debitNumber: String,
    paidCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
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
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        debitStepDescription?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
        }
        DetailRow(
            label = stringResource(CoreRes.string.label_installment_paid_count),
            value = "$paidCount از $totalCount",
            valueColor = colors.greenText,
            numeric = false,
        )
        DetailRow(
            label = stringResource(CoreRes.string.label_debit_number),
            value = debitNumber.orDash(),
            numeric = true,
        )
    }
}

/** One قسط row — number badge, amount, status pill + payment date. Read-only, as in legacy. */
@Composable
private fun InstallmentCard(
    item: InstallmentConstructionListPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val paid = item.paymentDate != null
    val (badgeBg, badgeFg) = if (paid) colors.greenBg to colors.greenText else colors.orangeBg to colors.orangeText

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
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InstallmentNumberBadge(number = item.debitSubCode.orDash(), background = badgeBg, contentColor = badgeFg)
            Spacer(modifier = Modifier.width(Spacing.sm))
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    NumericText(
                        text = (item.dtnAmount ?: 0L).toPriceFormat(),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                    )
                    Text(
                        text = stringResource(CoreRes.string.deferred_installment_rial),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = stringResource(CoreRes.string.label_payment_date),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                    NumericText(
                        text = item.paymentDate?.toFormattedDate().orDash(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textPrimary,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            StatusPill(text = item.lastPaymentSheetDescription.orDash(), containerColor = badgeBg, contentColor = badgeFg)
        }
    }
}

@Composable
private fun InstallmentNumberBadge(
    number: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        NumericText(
            text = number,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewInstallments = kotlinx.collections.immutable.persistentListOf(
    InstallmentConstructionListPR(
        workshopId = "2361847",
        debitNumber = "77640000001",
        debitSubCode = "1",
        dtnAmount = 214_000_000L,
        lastPaymentSheetAmount = 214_000_000L,
        dtnExpireDate = "14030201",
        lastPaymentSheetDescription = "پرداخت شده",
        paymentDate = "14030215",
    ),
    InstallmentConstructionListPR(
        workshopId = "2361847",
        debitNumber = "77640000001",
        debitSubCode = "4",
        dtnAmount = 214_000_000L,
        dtnExpireDate = "14031001",
        lastPaymentSheetDescription = "در انتظار پرداخت",
    ),
)

@PreviewRtlTheme
@Composable
private fun InstallmentManagementScreenPreview() {
    PreviewRtlThemeContent {
        InstallmentManagementScreen(
            state = InstallmentManagementUiState(
                fileNumber = 123804L,
                workshopId = "2361847",
                branchId = "7",
                debitNumber = "1402/64118",
                debitStepDescription = "مرحلهٔ اول تقسیط بدهی ساختمانی",
                items = PreviewInstallments,
            ),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InstallmentManagementScreenEmptyPreview() {
    PreviewRtlThemeContent {
        InstallmentManagementScreen(
            state = InstallmentManagementUiState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
