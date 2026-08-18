package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_empty_list

/**
 * The list every screen under کارگاه‌های کارفرما draws.
 *
 * All eight page, shimmer and end the same way, so the behaviour lives here once: a skeleton until
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
    contentPadding: PaddingValues = PaddingValues(Spacing.page),
    emptyMessage: String = stringResource(Res.string.workshop_empty_list),
    key: ((T) -> Any)? = null,
    header: (@Composable () -> Unit)? = null,
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

    if (state.isEmpty) {
        Column(
            modifier = modifier.fillMaxSize().padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            header?.invoke()
            EmptyStateMessage(icon = Icons.Outlined.Info, title = emptyMessage)
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
            .filter { it >= state.items.lastIndex - LOAD_MORE_THRESHOLD }
            .collect { onLoadMore() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        overscrollEffect = rememberJellyOverscroll(),
    ) {
        header?.let { item(key = HEADER_KEY) { it() } }

        itemsIndexed(
            items = state.items,
            key = key?.let { keyOf -> { _, item -> keyOf(item) } },
        ) { _, item -> row(item) }

        if (state.isLoadingMore) {
            item(key = FOOTER_KEY) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.height(FooterSpinnerSize))
                }
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
    rowCount: Int = SKELETON_ROWS,
    contentPadding: PaddingValues = PaddingValues(Spacing.page),
    header: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        header?.invoke()
        repeat(rowCount) {
            ShimmerBlock(
                modifier = Modifier.fillMaxWidth().height(SkeletonRowHeight),
                cornerRadius = CornerRadius.lg,
            )
        }
    }
}

private const val HEADER_KEY = "workshop-list-header"
private const val FOOTER_KEY = "workshop-list-footer"
private const val SKELETON_ROWS = 4
private const val LOAD_MORE_THRESHOLD = 2
private val SkeletonRowHeight = 132.dp
private val FooterSpinnerSize = 28.dp
