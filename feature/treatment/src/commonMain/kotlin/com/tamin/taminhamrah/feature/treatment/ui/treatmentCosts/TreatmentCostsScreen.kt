package com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.model.treatment.TreatmentCostPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.ic_tamin_misc_claims

/** Shown in place of an amount that has not loaded, so a blank never reads as zero. */
private const val UNKNOWN_AMOUNT = "—"

/** The endpoint's «پرداخت شده» marker. */
private const val PAID_STATUS = "1"

/**
 * «خسارت متفرقه» — the miscellaneous-claim certificates for the signed-in person.
 *
 * Its own destination in the treatment graph, so the system back button unwinds it; the hub no
 * longer hosts it through a screen-local flow flag.
 */
@Composable
fun TreatmentCostsScreen(
    onBack: () -> Unit,
    viewModel: TreatmentCostsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(CostsIntent.LoadList)
    }

    // «ارسال به صندوق» reports through the snackbar, the way the records screen reports its own
    // one-off outcomes — it does not belong in the list as a card that never goes away.
    val inboxResult = state.sendToInboxResult
    LaunchedEffect(inboxResult) {
        inboxResult?.let { snackbarHostState.showSnackbar(it) }
    }

    TreatmentCostsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onSendToInbox = { repId -> viewModel.sendIntent(CostsIntent.SendToInbox(repId)) },
        onRequestPdf = { repId -> viewModel.sendIntent(CostsIntent.DownloadPdf(repId)) },
        onDismissPdf = { viewModel.sendIntent(CostsIntent.DismissPdfViewer) },
        onRetry = { viewModel.sendIntent(CostsIntent.LoadList) },
    )
}

@Composable
fun TreatmentCostsContent(
    state: CostsUiState,
    onBack: () -> Unit,
    onSendToInbox: (String) -> Unit,
    onRequestPdf: (String) -> Unit,
    onDismissPdf: () -> Unit,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = LocalTaminColors.current

    // Which certificate is on screen. Opening the viewer no longer means a download has happened:
    // it decides for itself whether the file still needs fetching, so the tap only says which one.
    var showingRepId by remember { mutableStateOf<String?>(null) }

    val certificates = remember(state.treatmentCostList) {
        state.treatmentCostList.toImmutableList()
    }
    // Summed once per list change rather than on every recomposition.
    val total = remember(certificates) {
        certificates.sumOf { it.payPrice.toLongOrNull() ?: 0L }
    }

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = "خسارت متفرقه",
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = "بازگشت",
                        onClick = onBack,
                    )
                },
            )
        },
        bottomBar = {
            if (certificates.isNotEmpty()) {
                CostsTotalBar(amount = total.toPriceFormat(), count = certificates.size)
            }
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = onRetry,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading && certificates.isEmpty() -> item { CostsShimmerSkeleton() }

                    // A failed request and a genuinely empty result read very differently, so they
                    // get different states. Both recover the same way: pull to refresh.
                    state.error != null -> item { CostsErrorState(message = state.error) }

                    certificates.isEmpty() -> item {
                        TaminEmptyState(message = "خسارت متفرقه‌ای برای نمایش وجود ندارد.")
                    }

                    else -> itemsIndexed(certificates) { index, item ->
                        TreatmentCostCard(
                            item = item,
                            onSendToInbox = { onSendToInbox(item.repId) },
                            onOpenPdf = { showingRepId = item.repId },
                            modifier = Modifier
                                .padding(horizontal = Spacing.page)
                                .padding(
                                    top = if (index == 0) Spacing.md else 0.dp,
                                    bottom = if (index == certificates.lastIndex) {
                                        Spacing.md
                                    } else {
                                        Spacing.cardGap
                                    },
                                ),
                        )
                    }
                }
            }
        }
    }

    showingRepId?.let { repId ->
        TaminPdfViewer(
            // Named after the certificate, so one already downloaded is recognised and re-rendered
            // from the device instead of being fetched and saved twice.
            fileName = "treatment_cost_$repId.pdf",
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = { onRequestPdf(repId) },
            onDismiss = {
                showingRepId = null
                onDismissPdf()
            },
        )
    }
}

/**
 * One certificate: where it was issued, what it cost, and the two things that can be done with it.
 *
 * Built from the same parts as the records card — surface, status pill, numeric amount, divider,
 * action row — so the two lists read as one family.
 */
@Composable
private fun TreatmentCostCard(
    item: TreatmentCostPR,
    onSendToInbox: () -> Unit,
    onOpenPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val paid = item.payStatus == PAID_STATUS

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatusPill(
                text = item.payStatusDesc.ifBlank { item.statusDesc },
                containerColor = if (paid) colors.greenBg else colors.orangeBg,
                contentColor = if (paid) colors.greenText else colors.orangeText,
            )
            NumericText(
                text = item.serviceDate.toPersianDigits(),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
        }

        Text(
            text = item.healthcenterName,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "کد رهگیری ${item.rahgiriCode}".toPersianDigits(),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary,
        )

        TaminDivider(modifier = Modifier.padding(top = Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = "مبلغ",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textMuted,
                )
                // Number and unit are separate so «ریال» stays left of the digits under RTL.
                NumericText(
                    text = item.payPrice.toLongOrNull()?.toPriceFormat() ?: UNKNOWN_AMOUNT,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                )
                Text(
                    text = "ریال",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                CostAction(
                    icon = vectorResource(Res.drawable.ic_tamin_misc_claims),
                    contentDescription = "ارسال به صندوق پیام",
                    containerColor = colors.blueBg,
                    contentColor = colors.blueText,
                    onClick = onSendToInbox,
                )
                CostAction(
                    icon = vectorResource(Res.drawable.ic_tamin_download),
                    contentDescription = "دریافت گواهی",
                    containerColor = colors.greenBg,
                    contentColor = colors.teal,
                    onClick = onOpenPdf,
                )
            }
        }
    }
}

/** A single round action on the card; the label lives in the content description, as on the hub. */
@Composable
private fun CostAction(
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(ACTION_SIZE)
            .background(containerColor, RoundedCornerShape(CornerRadius.chip))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

/** Total spend across the listed certificates, pinned under the list like the records totals. */
@Composable
private fun CostsTotalBar(amount: String, count: Int) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgSurface)
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "جمع $count مورد".toPersianDigits(),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            NumericText(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                color = colors.teal,
            )
            Text(
                text = "ریال",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
        }
    }
}

/**
 * Failure state: what went wrong and how to recover, nothing more.
 *
 * No retry button — the list is pull-to-refresh, so one gesture both reloads a good list and
 * recovers from a failure, instead of the screen offering two ways to do the same thing.
 */
@Composable
private fun CostsErrorState(message: String) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(Spacing.page),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_cross),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.large),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "برای تلاش دوباره، صفحه را به پایین بکشید.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CostsShimmerSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        repeat(SKELETON_ROWS) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SKELETON_ROW_HEIGHT)
                    .taminSurface(CornerRadius.card)
                    .shimmer(),
            )
        }
    }
}

private val ACTION_SIZE = 36.dp
private val SKELETON_ROW_HEIGHT = 120.dp
private const val SKELETON_ROWS = 4

@PreviewRtlTheme
@Composable
fun TreatmentCostsContentPreview() {
    PreviewRtlThemeContent {
        TreatmentCostsContent(
            state = TreatmentMocks.costsUiState,
            onBack = {},
            onSendToInbox = {},
            onRequestPdf = {},
            onDismissPdf = {},
            onRetry = {},
        )
    }
}
