package com.tamin.taminhamrah.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_search_placeholder
import taminx.core.core_ui.no_items_found

private val DefaultListHeight = 400.dp
private val DefaultLoadingHeight = 200.dp

/**
 * Simple key-value model for sheet items when a custom domain model is not used.
 */
@Immutable
data class TaminOptionSheetItem(
    val id: String,
    val label: String,
)

/**
 * Generic modal bottom sheet for searching and selecting an item from a list (e.g. cities, recipients, branches, organizations).
 *
 * Displays:
 * - Drag handle at top
 * - Centered title
 * - Search bar (supports internal auto-filtering or external search query delegation)
 * - List of items with thin dividers
 * - Loading indicator or empty state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> TaminSearchableListSheet(
    title: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    itemKey: ((T) -> Any)? = null,
    searchQuery: String? = null,
    onSearchQueryChange: ((String) -> Unit)? = null,
    searchPlaceholder: String = stringResource(Res.string.active_relation_search_placeholder),
    showSearch: Boolean = true,
    isLoading: Boolean = false,
    emptyMessage: String = stringResource(Res.string.no_items_found),
    listHeight: Dp = DefaultListHeight,
    loadingHeight: Dp = DefaultLoadingHeight,
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        dragHandle = {
            SheetDragHandle()
        },
        modifier = modifier,
    ) {
        SearchableListSheetContent(
            title = title,
            items = items,
            itemLabel = itemLabel,
            onItemSelected = onItemSelected,
            itemKey = itemKey,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            searchPlaceholder = searchPlaceholder,
            showSearch = showSearch,
            isLoading = isLoading,
            emptyMessage = emptyMessage,
            listHeight = listHeight,
            loadingHeight = loadingHeight,
            showDragHandle = false,
        )
    }
}

/**
 * Overload for simple string/ID items represented as [TaminOptionSheetItem].
 */
@Composable
fun TaminSearchableListSheet(
    title: String,
    items: List<TaminOptionSheetItem>,
    onItemSelected: (TaminOptionSheetItem) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String? = null,
    onSearchQueryChange: ((String) -> Unit)? = null,
    searchPlaceholder: String = stringResource(Res.string.active_relation_search_placeholder),
    showSearch: Boolean = true,
    isLoading: Boolean = false,
    emptyMessage: String = stringResource(Res.string.no_items_found),
    listHeight: Dp = DefaultListHeight,
    loadingHeight: Dp = DefaultLoadingHeight,
) {
    TaminSearchableListSheet(
        title = title,
        items = items,
        itemLabel = { it.label },
        itemKey = { it.id },
        onItemSelected = onItemSelected,
        onDismiss = onDismiss,
        modifier = modifier,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = searchPlaceholder,
        showSearch = showSearch,
        isLoading = isLoading,
        emptyMessage = emptyMessage,
        listHeight = listHeight,
        loadingHeight = loadingHeight,
    )
}

/**
 * The inner content of [TaminSearchableListSheet], usable standalone or for previews.
 */
@Composable
fun <T> SearchableListSheetContent(
    title: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemKey: ((T) -> Any)? = null,
    searchQuery: String? = null,
    onSearchQueryChange: ((String) -> Unit)? = null,
    searchPlaceholder: String = stringResource(Res.string.active_relation_search_placeholder),
    showSearch: Boolean = true,
    isLoading: Boolean = false,
    emptyMessage: String = stringResource(Res.string.no_items_found),
    listHeight: Dp = DefaultListHeight,
    loadingHeight: Dp = DefaultLoadingHeight,
    showDragHandle: Boolean = false,
) {
    val taminColors = LocalTaminColors.current
    var localQuery by remember { mutableStateOf("") }
    val currentQuery = searchQuery ?: localQuery

    val filteredItems = remember(items, currentQuery, showSearch, onSearchQueryChange) {
        if (!showSearch || currentQuery.isBlank() || onSearchQueryChange != null) {
            items
        } else {
            items.filter { itemLabel(it).contains(currentQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showDragHandle) {
            SheetDragHandle()
        }

        TaminText(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary,
            modifier = Modifier.padding(Spacing.lg),
        )

        if (showSearch) {
            CustomSearchBar(
                query = currentQuery,
                onQueryChange = { newQuery ->
                    localQuery = newQuery
                    onSearchQueryChange?.invoke(newQuery)
                },
                placeHolder = searchPlaceholder,
                modifier = Modifier.padding(horizontal = Spacing.lg),
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(loadingHeight),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = taminColors.blueText)
            }

            filteredItems.isEmpty() -> TaminEmptyState(
                message = emptyMessage,
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(listHeight),
                contentPadding = PaddingValues(bottom = Spacing.xxl),
            ) {
                items(
                    items = filteredItems,
                    key = itemKey?.let { keyFn -> { item: T -> keyFn(item) } },
                ) { item ->
                    SearchableListItemRow(
                        label = itemLabel(item),
                        onClick = { onItemSelected(item) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                        thickness = 0.5.dp,
                        color = taminColors.border,
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetDragHandle() {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .padding(vertical = Spacing.md)
            .size(width = 32.dp, height = 4.dp)
            .background(taminColors.border, RoundedCornerShape(50))
    )
}

@Composable
private fun SearchableListItemRow(
    label: String,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        contentAlignment = Alignment.CenterStart,
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = taminColors.textPrimary,
        )
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun TaminSearchableListSheetPreview() {
    val sampleItems = listOf(
        "دادگاه عمومی",
        "دادگاه انقلاب",
        "دادگاه نظامی",
        "دادگاه اطفال و نوجوانان",
        "دادگاه تجدیدنظر",
        "دادسراي عمومي و انقلاب",
        "دادسراي ويژه روحانيت",
    )
    PreviewRtlThemeContent {
        SearchableListSheetContent(
            title = "لطفاً نهاد دریافت‌کننده را انتخاب نمایید",
            items = sampleItems,
            itemLabel = { it },
            onItemSelected = {},
            showDragHandle = true,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminSearchableListSheetDarkPreview() {
    val sampleItems = listOf(
        "تهران",
        "مشهد",
        "اصفهان",
        "شیراز",
        "تبریز",
        "اهواز",
        "کرمان",
    )
    PreviewRtlThemeContent(darkTheme = true) {
        SearchableListSheetContent(
            title = "لطفاً شهر را انتخاب نمایید",
            items = sampleItems,
            itemLabel = { it },
            onItemSelected = {},
            searchPlaceholder = "جستجوی شهر...",
            showDragHandle = true,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminSearchableListSheetLoadingPreview() {
    PreviewRtlThemeContent {
        SearchableListSheetContent(
            title = "لطفاً نهاد دریافت‌کننده را انتخاب نمایید",
            items = emptyList<String>(),
            itemLabel = { it },
            onItemSelected = {},
            isLoading = true,
            showDragHandle = true,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminSearchableListSheetEmptyPreview() {
    PreviewRtlThemeContent {
        SearchableListSheetContent(
            title = "لطفاً نهاد دریافت‌کننده را انتخاب نمایید",
            items = emptyList<String>(),
            itemLabel = { it },
            onItemSelected = {},
            isLoading = false,
            emptyMessage = "موردی یافت نشد",
            showDragHandle = true,
        )
    }
}
