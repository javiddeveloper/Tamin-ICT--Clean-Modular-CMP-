package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.ComputationalBaseKeys
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.mapper.workshop.declaredTotal
import com.tamin.taminhamrah.model.workshop.ComputationalBasePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_base_document_badge
import taminx.core.core_ui.assigner_base_documents_count
import taminx.core.core_ui.assigner_base_period
import taminx.core.core_ui.assigner_bases_empty_body
import taminx.core.core_ui.assigner_bases_empty_title
import taminx.core.core_ui.assigner_bases_title
import taminx.core.core_ui.assigner_bases_total
import taminx.core.core_ui.assigner_contract_subtitle
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_computational_base

/**
 * مبانی محاسباتی — the bases filed under one پیمان, and what they declare in total.
 *
 * The four keys the service is addressed with travel in the **route**, not in shared state, so the
 * screen can refetch after process death and cannot be opened against a پیمان it was not given.
 * The service reads a missing key as "no filter on that column" and answers with another
 * contract's bases, which is why the action that opens this is disabled when any of the four is
 * blank rather than being allowed through to a wrong-looking list.
 */
@Composable
fun ComputationalBasesScreen(
    viewModel: AssignerContractsViewModel,
    workshopId: String,
    branchCode: String,
    contractRow: String,
    contractSequence: String,
    workshopName: String,
    rowLabel: String,
    onBack: () -> Unit,
    onOpenBaseDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // One value rather than four, so the effect's key is one comparison and the ViewModel can tell
    // "the same پیمان, come back to" from "a different one, refetch" without four fields of its own.
    val keys = remember(workshopId, branchCode, contractRow, contractSequence) {
        ComputationalBaseKeys(workshopId, branchCode, contractRow, contractSequence)
    }
    LaunchedEffect(keys) { viewModel.sendIntent(AssignerContractsIntent.OpenBases(keys)) }

    ComputationalBasesContent(
        bases = state.bases,
        workshopName = workshopName,
        rowLabel = rowLabel,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onOpenBaseDetail = onOpenBaseDetail,
        modifier = modifier,
    )
}

@Composable
fun ComputationalBasesContent(
    bases: PagedListState<ComputationalBasePR>,
    workshopName: String,
    rowLabel: String,
    onIntent: (AssignerContractsIntent) -> Unit,
    onBack: () -> Unit,
    onOpenBaseDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    // A sum of the rows in hand would be a smaller number presented as the whole, so the tile waits
    // until the last page is in.
    val items = bases.items
    val isComplete = !bases.isLoading && !bases.isLoadingMore && !bases.hasMore
    val total = remember(items, isComplete) { if (isComplete) items.declaredTotal() else null }

    WorkshopScreenShell(
        title = stringResource(Res.string.assigner_bases_title),
        onBack = onBack,
        subtitle = stringResource(Res.string.assigner_contract_subtitle, workshopName, rowLabel),
        modifier = modifier,
    ) {
        // The total keeps its place through every list state.
        val totalLabel = stringResource(Res.string.assigner_bases_total)
        WorkshopListScaffold(
            header = {
                // The design's tile: a caption over the figure, both at the start edge.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .taminSurface(CornerRadius.xl)
                        .padding(horizontal = Spacing.smd, vertical = Spacing.smd),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = totalLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                    if (total != null) {
                        NumericText(
                            text = total,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(TOTAL_SHIMMER_WIDTH)
                                .height(IconSize.small)
                                .clip(RoundedCornerShape(CornerRadius.md))
                                .shimmer(),
                        )
                    }
                }
            },
            state = bases,
            onLoadMore = { onIntent(AssignerContractsIntent.LoadMoreBases) },
            onRetry = { onIntent(AssignerContractsIntent.RetryBases) },
            // A new پیمان plays the row entrance again; paging further into one does not.
            entranceKey = rowLabel,
            key = { it.letterNumber },
            empty = {
                EmptyStateMessage(
                    icon = vectorResource(Res.drawable.ic_tamin_computational_base),
                    title = stringResource(Res.string.assigner_bases_empty_title),
                    subtitle = stringResource(Res.string.assigner_bases_empty_body),
                    showIconTile = true,
                )
            },
        ) { base, itemModifier ->
            ComputationalBaseRow(
                base = base,
                // جزئیات مبنا is addressed by شمارهٔ سند and finds its own row in this same list,
                // so nothing has to be put into state before that screen composes.
                onOpen = { onOpenBaseDetail(base.letterNumber) },
                modifier = itemModifier,
            )
        }
    }
}

/**
 * One مبنا as the design's card draws it: the period it covers, its «سند N» pill and how many
 * documents it carries under that, then its amount and a chevron.
 *
 * A base the service sent no period for is titled by its تاریخ ارسال instead, the one date it does
 * carry.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ComputationalBaseRow(
    base: ComputationalBasePR,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val period = stringResource(Res.string.assigner_base_period, base.periodStart, base.periodEnd)
    val title = if (base.periodStart.isNotBlank() && base.periodEnd.isNotBlank()) period else base.sendDate
    val shape = remember { RoundedCornerShape(WorkshopDimens.cardCorner) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .clip(shape)
            .clickable(onClick = onOpen)
            .padding(BaseRowPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            // A period is longer than the design's month, so the سند pill moves under it, beside
            // the count, and the two wrap rather than squeeze each other to an ellipsis.
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                StatusPill(
                    text = stringResource(Res.string.assigner_base_document_badge, base.letterNumber),
                    containerColor = colors.bgPage,
                    contentColor = colors.textSecondary,
                    verticalPadding = Spacing.xs,
                )
                // The count in the blue pill profile gives its افراد تبعی.
                StatusPill(
                    text = stringResource(Res.string.assigner_base_documents_count, base.documentCount),
                    containerColor = colors.blueBg,
                    contentColor = colors.blueText,
                    verticalPadding = Spacing.xs,
                )
            }
        }
        NumericText(
            text = base.amount,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.chevron,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

/** `padding:11px 12px` inside the design's base row. */
private val BaseRowPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.smd)

/** How much of the tile's width the total's placeholder takes while the last page is still coming. */
private const val TOTAL_SHIMMER_WIDTH = 0.5f

// ------------------------------------------------------------------------------- previews

private val PreviewBases = persistentListOf(
    ComputationalBasePR(
        letterNumber = "۱۲۰۴۴",
        sendDate = "۱۴۰۰/۱۲/۱۵",
        amount = "۸۴,۰۰۰,۰۰۰ ریال",
        amountRials = 84_000_000L,
        periodStart = "۱۴۰۰/۰۷/۰۱",
        periodEnd = "۱۴۰۰/۰۹/۳۰",
        documentCount = "۲",
    ),
    // No period sent — titled by its تاریخ ارسال.
    ComputationalBasePR(
        letterNumber = "۱۲۱۹۰",
        sendDate = "۱۴۰۱/۰۱/۲۰",
        amount = "۹۱,۵۰۰,۰۰۰ ریال",
        amountRials = 91_500_000L,
        documentCount = "۱",
    ),
)

@PreviewRtlTheme
@Composable
private fun ComputationalBasesFilledPreview() = PreviewRtlThemeContent {
    ComputationalBasesContent(
        bases = PagedListState(items = PreviewBases, total = 2),
        workshopName = "دبستان کارن ۲ مجتبی غلامیان",
        rowLabel = "۱",
        onIntent = {},
        onBack = {},
        onOpenBaseDetail = {},
    )
}

/** A پیمان with no bases filed — a state the design's own seed data reaches on purpose. */
@PreviewRtlTheme
@Composable
private fun ComputationalBasesEmptyPreview() = PreviewRtlThemeContent {
    ComputationalBasesContent(
        bases = PagedListState(),
        workshopName = "شرکت راه‌سازی البرز شرق",
        rowLabel = "۳",
        onIntent = {},
        onBack = {},
        onOpenBaseDetail = {},
    )
}

@PreviewRtlTheme
@Composable
private fun ComputationalBasesLoadingPreview() = PreviewRtlThemeContent {
    ComputationalBasesContent(
        bases = PagedListState(isLoading = true),
        workshopName = "دبستان کارن ۲ مجتبی غلامیان",
        rowLabel = "۱",
        onIntent = {},
        onBack = {},
        onOpenBaseDetail = {},
    )
}
