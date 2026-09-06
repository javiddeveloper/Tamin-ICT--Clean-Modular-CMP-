package com.tamin.taminhamrah.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.SheetDimens
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_search_placeholder
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.no_items_found

private val DefaultListHeight = 400.dp
private val DefaultLoadingHeight = 200.dp

@Immutable
data class TaminOptionSheetItem(
    val id: String,
    val label: String,
)

/**
 * Modal sheet for picking one labeled option from a (possibly long) list.
 * When [showSearch] is true, filters [items] offline by [TaminOptionSheetItem.label].
 *
 * Use this overload when the caller already has key/label pairs and wants a selected-state
 * checkmark row (e.g. ill-days wizard branch/city pickers).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaminSearchableOptionSheet(
    title: String,
    items: List<TaminOptionSheetItem>,
    selectedId: String?,
    onSelect: (TaminOptionSheetItem) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    searchPlaceholder: String = stringResource(Res.string.active_relation_search_placeholder),
    showSearch: Boolean = true,
    /**
     * When non-null, search is delegated to the caller (e.g. remote city filter).
     * Local contains-filter is skipped so the parent can replace [items].
     */
    onSearchQueryChange: ((String) -> Unit)? = null,
) {
    val colors = LocalTaminColors.current
    var query by remember { mutableStateOf("") }
    val filtered = remember(items, query, showSearch, onSearchQueryChange) {
        if (!showSearch || query.isBlank() || onSearchQueryChange != null) {
            items
        } else {
            items.filter { it.label.contains(query, ignoreCase = true) }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
            Spacer(modifier = Modifier.height(Spacing.lg))
            if (showSearch) {
                CustomSearchBar(
                    query = query,
                    onQueryChange = {
                        query = it
                        onSearchQueryChange?.invoke(it)
                    },
                    placeHolder = searchPlaceholder,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SheetDimens.listMaxHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.no_items_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textTertiary,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = SheetDimens.listMaxHeight),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(
                        items = filtered,
                        key = { it.id },
                    ) { option ->
                        OptionRow(
                            label = option.label,
                            selected = option.id == selectedId,
                            onClick = { onSelect(option) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Generic searchable list sheet for domain models (cities, recipients, branches, …).
 *
 * Supports internal filtering or external search delegation, loading, and empty states.
 * Prefer this overload when the caller has a typed list rather than [TaminOptionSheetItem].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> TaminSearchableOptionSheet(
    title: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit,
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
        dragHandle = { SheetDragHandle() },
        modifier = modifier,
    ) {
        SearchableOptionListContent(
            title = title,
            items = items,
            itemLabel = itemLabel,
            onItemSelected = onSelect,
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

@Composable
fun <T> SearchableOptionListContent(
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
private fun OptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(if (selected) colors.blueBg else colors.bgSurface)
            .border(
                width = Thickness.border,
                color = if (selected) colors.blueText else colors.border,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.small),
            )
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

@PreviewRtlTheme
@Composable
private fun TaminSearchableOptionSheetListPreview() {
    val sampleItems = listOf(
        "دادگاه عمومی",
        "دادگاه انقلاب",
        "دادگاه نظامی",
        "دادگاه اطفال و نوجوانان",
        "دادگاه تجدیدنظر",
    )
    PreviewRtlThemeContent {
        SearchableOptionListContent(
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
private fun TaminSearchableOptionSheetLoadingPreview() {
    PreviewRtlThemeContent {
        SearchableOptionListContent(
            title = "لطفاً نهاد دریافت‌کننده را انتخاب نمایید",
            items = emptyList<String>(),
            itemLabel = { it },
            onItemSelected = {},
            isLoading = true,
            showDragHandle = true,
        )
    }
}
