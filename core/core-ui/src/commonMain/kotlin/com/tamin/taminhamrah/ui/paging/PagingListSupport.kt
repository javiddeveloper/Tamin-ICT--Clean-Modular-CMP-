package com.tamin.taminhamrah.ui.paging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_retry
import kotlin.time.Duration.Companion.milliseconds

private const val DEFAULT_PREFETCH_DISTANCE = 2

@Composable
fun LazyListState.OnLoadMore(
    enabled: Boolean = true,
    prefetchDistance: Int = DEFAULT_PREFETCH_DISTANCE,
    onLoadMore: () -> Unit,
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)

    LaunchedEffect(this, enabled, prefetchDistance) {
        if (!enabled) return@LaunchedEffect
        snapshotFlow {
            val info = layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false
            info.totalItemsCount > 0 && lastVisibleIndex >= info.totalItemsCount - 1 - prefetchDistance
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { currentOnLoadMore() }
    }
}

/** How long typing has to pause before a server-side search is sent. */
private const val SEARCH_DEBOUNCE_MILLIS = 300L

/**
 * The text a server-side search field shows, handed to [onQueryChange] only once typing pauses.
 *
 * Without the pause every keystroke is a request of its own, against lists that run to hundreds
 * of thousands of rows. The field reads and writes the returned state; [query] seeds it and is
 * what an unchanged text is compared against, so opening a sheet does not re-send its query.
 */
@Composable
fun rememberDebouncedQuery(
    query: String,
    onQueryChange: (String) -> Unit,
    delayMillis: Long = SEARCH_DEBOUNCE_MILLIS,
): MutableState<String> {
    val text = remember { mutableStateOf(query) }
    val currentQuery by rememberUpdatedState(query)
    val currentOnQueryChange by rememberUpdatedState(onQueryChange)
    LaunchedEffect(text.value, delayMillis) {
        if (text.value == currentQuery) return@LaunchedEffect
        delay(delayMillis.milliseconds)
        currentOnQueryChange(text.value)
    }
    return text
}

@Composable
fun PagingFooter(
    isLoadingNextPage: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    when {
        isLoadingNextPage -> Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(Spacing.xl),
                strokeWidth = 2.dp,
                color = colors.textMuted,
            )
        }

        error != null -> Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.action_retry),
                onClick = onRetry,
                height = 40.dp,
            )
        }
    }
}
