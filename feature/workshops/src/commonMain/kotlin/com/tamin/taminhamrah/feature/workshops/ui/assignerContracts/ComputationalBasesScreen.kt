package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.ComputationalBaseKeys
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.ComputationalBasePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_base_letter_number
import taminx.core.core_ui.assigner_base_summary
import taminx.core.core_ui.assigner_bases_empty_body
import taminx.core.core_ui.assigner_bases_empty_title
import taminx.core.core_ui.assigner_bases_title
import taminx.core.core_ui.assigner_contract_subtitle
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_computational_base

/**
 * مبانی محاسباتی — the bases filed under one پیمان.
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
    WorkshopScreenShell(
        title = stringResource(Res.string.assigner_bases_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        // The design puts the پیمان's identity as a muted line above the list rather than in
        // the hero, and it keeps its place through every list state.
        val subtitle = stringResource(
            Res.string.assigner_contract_subtitle,
            workshopName,
            rowLabel,
        )
        WorkshopListScaffold(
            header = {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalTaminColors.current.textMuted,
                    modifier = Modifier.fillMaxWidth(),
                )
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
 * One مبنا: its سند number, when it was sent, how many documents hang off it, and its amount.
 *
 * Drawn through the shared [ListGroupView] rather than a fourth bespoke card in this feature — the
 * design's row is a title, a muted line under it, a trailing value and a chevron, which is exactly
 * what a list row already is.
 *
 * The design labels the middle line «دوره». The service sends no such column; `senddate` — the
 * «تاریخ ارسال مبانی محاسباتی» the old app puts on this same row — is what actually arrives, so
 * that is what is shown, with the document count beside it as the design has it.
 */
@Composable
private fun ComputationalBaseRow(
    base: ComputationalBasePR,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val title = stringResource(Res.string.assigner_base_letter_number, base.letterNumber)
    val summary = stringResource(
        Res.string.assigner_base_summary,
        base.sendDate,
        base.documentCount,
    )
    val chevron = vectorResource(Res.drawable.ic_tamin_chevron_forward)
    val amount = base.amount

    // Built inside a remember for the same reason the scaffold's entrance key is: a fresh
    // ListItemData every recomposition would cost ListGroupView its ability to skip, and the
    // trailing slot is a lambda that would be a new instance each time.
    val items = remember(title, summary, amount, chevron, colors, onOpen) {
        persistentListOf(
            ListItemData(
                title = title,
                subtitle = summary,
                onClick = onOpen,
                customTrailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NumericText(
                            text = amount,
                            style = MaterialTheme.typography.labelMedium
                                .copy(fontWeight = FontWeight.Bold),
                            color = colors.blueText,
                        )
                        Icon(
                            imageVector = chevron,
                            contentDescription = null,
                            tint = colors.chevron,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                },
            )
        )
    }

    ListGroupView(
        items = items,
        showDividers = false,
        itemContentPadding = BaseRowPadding,
        modifier = modifier.fillMaxWidth(),
    )
}

/** `padding:12px 14px` inside the design's base row. */
private val BaseRowPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.smd)

// ------------------------------------------------------------------------------- previews

private val PreviewBases = persistentListOf(
    ComputationalBasePR(
        letterNumber = "۱۲۰۴۴",
        sendDate = "۱۴۰۰/۱۲/۱۵",
        amount = "۸۴,۰۰۰,۰۰۰ ریال",
        documentCount = "۲",
    ),
    ComputationalBasePR(
        letterNumber = "۱۲۱۹۰",
        sendDate = "۱۴۰۱/۰۱/۲۰",
        amount = "۹۱,۵۰۰,۰۰۰ ریال",
        documentCount = "۱",
    ),
)

@PreviewRtlTheme
@Composable
private fun ComputationalBasesFilledPreview() = PreviewRtlThemeContent {
    ComputationalBasesContent(
        bases = PagedListState(items = PreviewBases, total = 2),
        workshopName = "دبستان کارن ۲ مجتبی غلامیان",
        rowLabel = "ردیف ۱",
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
        rowLabel = "ردیف ۳",
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
        rowLabel = "ردیف ۱",
        onIntent = {},
        onBack = {},
        onOpenBaseDetail = {},
    )
}
