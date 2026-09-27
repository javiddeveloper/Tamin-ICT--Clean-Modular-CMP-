package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.paging.OnLoadMore
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_my_workshops

/**
 * The parts every کارگاه bottom sheet in this design opens with.
 *
 * Every sheet here switches `ModalBottomSheet`'s own `dragHandle` off and draws the design's
 * grabber instead, then a centred title and an optional line under it. Three sheets drew that same
 * opening; this is it declared once.
 */

/** The design's own grabber — `width:44px; height:4px; border-radius:100px`. */
@Composable
fun WorkshopSheetGrabber(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .size(
                width = WorkshopDimens.contractRowGrabberWidth,
                height = WorkshopDimens.contractRowGrabberHeight,
            )
            .background(colors.border, RoundedCornerShape(CornerRadius.full)),
    )
}

/**
 * A sheet's body: page insets, the navigation-bar inset, the grabber, a title, and its content.
 *
 * The body itself does not scroll: [WorkshopQuickPickList] is the one part that does, and it gives
 * way with `weight(fill = false)`, so on a short screen the list shrinks rather than pushing the
 * sheet's primary button past the bottom edge.
 */
@Composable
fun WorkshopSheetBody(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page)
            .padding(
                top = Spacing.smd,
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + Spacing.lg,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        WorkshopSheetGrabber(modifier = Modifier.padding(bottom = Spacing.smd))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
        content()
    }
}

/**
 * کارگاه‌های شما — the employer's own workshops, offered above a sheet's code fields and paged in as
 * the list is scrolled.
 *
 * Both search sheets under کارگاه‌های کارفرما ask for a کد کارگاه, and typing a ten-digit number
 * from memory is the worst part of either. Picking a row fills the code *and* its branch in one go.
 *
 * The list is the sheet's only scrolling part, and it takes what height is left rather than all it
 * could, so the fields above and the button below stay put. Absent rather than empty when there is
 * nothing to offer: a heading over no rows reads as a list that failed to load.
 */
@Composable
fun ColumnScope.WorkshopQuickPickList(
    workshops: PagedListState<WorkshopPR>,
    selectedWorkshopId: String,
    selectedBranchCode: String,
    onPick: (workshopId: String, branchCode: String) -> Unit,
    onLoadMore: () -> Unit,
    /**
     * Shows the next page arriving as a shimmering row instead of the paging spinner. False — the
     * default — keeps the spinner ردیف‌های پیمان shows.
     */
    shimmerLoadingMore: Boolean = false,
    /**
     * Draws each unselected row as a bordered surface card, the way the document-type sheet lists its
     * choices, for a sheet on the page color. False — the default — keeps the gray chip rows.
     */
    cardRows: Boolean = false,
) {
    val listState = rememberLazyListState()
    listState.OnLoadMore(
        enabled = workshops.canLoadMore,
        onLoadMore = onLoadMore,
    )
    if (workshops.items.isEmpty()) return
    val colors = LocalTaminColors.current
    Text(
        text = stringResource(Res.string.contract_rows_my_workshops),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = colors.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.smd, bottom = Spacing.sm),
    )
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
        verticalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTileGap),
    ) {
        items(
            items = workshops.items,
            key = { "${it.workshopId}_${it.branchCode}" },
        ) { workshop ->
            WorkshopQuickPickRow(
                name = workshop.name,
                codeLabel = workshop.codeLabel,
                isSelected = workshop.workshopId == selectedWorkshopId &&
                    (selectedBranchCode.isBlank() || workshop.branchCode == selectedBranchCode),
                onPick = { onPick(workshop.workshopId, workshop.branchCode) },
                cardRows = cardRows,
            )
        }
        if (workshops.isLoadingMore) {
            item(key = LOADING_MORE_KEY) {
                if (shimmerLoadingMore) {
                    ShimmerBlock(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ShimmerSize.fieldHeight),
                        cornerRadius = CornerRadius.md,
                    )
                } else {
                    PagingFooter(isLoadingNextPage = true, error = null, onRetry = {})
                }
            }
        }
    }
}

/** One کارگاه‌های شما row: picking it fills both fields at once, branch included. */
@Composable
private fun WorkshopQuickPickRow(
    name: String,
    codeLabel: String,
    isSelected: Boolean,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
    cardRows: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val shape = remember(cardRows) { RoundedCornerShape(if (cardRows) CornerRadius.lg else CornerRadius.md) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onPick)
            .background(
                when {
                    isSelected -> colors.blueBg
                    cardRows -> colors.bgSurface
                    else -> colors.chipBg
                },
            )
            .then(
                when {
                    isSelected -> Modifier.border(Thickness.border, colors.blueBorder, shape)
                    cardRows -> Modifier.border(Thickness.border, colors.border, shape)
                    else -> Modifier
                },
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) colors.blueText else colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        NumericText(
            text = codeLabel,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) colors.blueText else colors.textMuted,
        )
    }
}

/** The footer row's key; the workshop rows are keyed `workshopId_branchCode`, so it cannot clash. */
private const val LOADING_MORE_KEY = "loading_more"
