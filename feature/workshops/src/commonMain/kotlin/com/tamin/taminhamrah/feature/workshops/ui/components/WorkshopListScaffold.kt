package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.retry
import taminx.core.core_ui.workshop_empty_list
import taminx.core.core_ui.workshop_error_receive_data

/**
 * The list every screen under کارگاه‌های کارفرما draws.
 *
 * All eight page, shimmer and end the same way, so the behavior lives here once: a skeleton until
 * the first page lands, an empty state when the service answers with nothing, the rubber-band
 * overscroll the rest of the app uses, and a request for the next page raised from the scroll
 * position rather than from a button.
 *
 * Only the row is passed in. Anything that varies per screen — headers, sheets, per-row actions —
 * is the caller's, which is what keeps this from growing a flag per screen.
 */
@Composable
fun <T> WorkshopListScaffold(
    state: PagedListState<T>,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = WorkshopDimens.listContentPadding,
    emptyMessage: String = stringResource(Res.string.workshop_empty_list),
    key: ((T) -> Any)? = null,
    header: (@Composable () -> Unit)? = null,
    /**
     * What stands in for the list when the service answers with nothing.
     *
     * Null — the default every existing caller takes — draws [emptyMessage] as a plain title.
     * ردیف‌های پیمان passes its own, because it has two different reasons to be empty ("no workshop
     * chosen yet" and "this workshop has no rows") and the design words and illustrates them
     * differently.
     */
    empty: (@Composable () -> Unit)? = null,
    /**
     * Offered beside the failure message. Null — the default — states the failure without one,
     * which is what a caller with no cheap way to re-run the request should do.
     */
    onRetry: (() -> Unit)? = null,
    row: @Composable (T) -> Unit,
) {
    // The three states share one set of insets: a header that keeps the page margins while the
    // list is loading, then loses them once the rows arrive, reads as the page jumping sideways.
    if (state.isFirstLoad) {
        WorkshopListSkeleton(
            modifier = modifier,
            contentPadding = contentPadding,
            header = header,
        )
        return
    }

    // Before the empty branch, because a failed list is not an empty one. Without this the list
    // fell through to the LazyColumn with nothing in it and drew a blank page — a 404 and a
    // workshop with no rows looked identical.
    if (state.isFailed) {
        Column(
            modifier = modifier.fillMaxSize().padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            header?.invoke()
            EmptyStateMessage(
                icon = Icons.Outlined.Warning,
                // The service's own words when it gave any, the generic line when it did not.
                title = state.error?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.workshop_error_receive_data),
                actionLabel = onRetry?.let { stringResource(Res.string.retry) },
                onAction = onRetry,
                showIconTile = true,
            )
        }
        return
    }

    if (state.isEmpty) {
        Column(
            modifier = modifier.fillMaxSize().padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            header?.invoke()
            if (empty != null) empty() else {
                EmptyStateMessage(icon = Icons.Outlined.Info, title = emptyMessage)
            }
        }
        return
    }

    // The next page is asked for while the last few rows are still below the fold, so the list
    // grows before the user reaches the bottom. Read through snapshotFlow, not during composition:
    // scroll position changes every frame, and reading it here would recompose the whole list.
    val canLoadMore = state.canLoadMore
    LaunchedEffect(listState, canLoadMore) {
        if (!canLoadMore) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .distinctUntilChanged()
            .filter { it >= state.items.lastIndex - WorkshopConstants.LOAD_MORE_THRESHOLD }
            .collect { onLoadMore() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        overscrollEffect = rememberJellyOverscroll(),
    ) {
        header?.let { item(key = WorkshopConstants.HEADER_KEY) { it() } }

        itemsIndexed(
            items = state.items,
            // The position is part of the key, because none of these lists has a field guaranteed
            // to be unique: an employer can hold two agreements for one workshop, and
            // `workshopId + branchCode` then repeats — which Compose treats as a fatal
            // "Key was already used" rather than a display glitch. Prefixing the index keeps the
            // caller's key meaningful while making a collision impossible, and these lists only
            // ever grow at the end, so an item's index — and therefore its identity — is stable.
            key = key?.let { keyOf -> { index, item -> "$index:${keyOf(item)}" } },
        ) { _, item -> row(item) }

        if (state.isLoadingMore) {
            item(key = WorkshopConstants.FOOTER_KEY) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(WorkshopDimens.skeletonRowHeight),
                    cornerRadius = CornerRadius.lg,
                )
            }
        }
    }
}

/**
 * What stands in for the list until the first page arrives.
 *
 * Card-shaped blocks rather than a spinner, so the page does not jump when the rows replace them.
 */
@Composable
fun WorkshopListSkeleton(
    modifier: Modifier = Modifier,
    rowCount: Int = WorkshopConstants.SKELETON_ROWS,
    contentPadding: PaddingValues = WorkshopDimens.listContentPadding,
    header: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        header?.invoke()
        repeat(rowCount) {
            ShimmerBlock(
                modifier = Modifier.fillMaxWidth().height(WorkshopDimens.skeletonRowHeight),
                cornerRadius = CornerRadius.lg,
            )
        }
    }
}

