package com.tamin.taminhamrah.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.tamin.taminhamrah.ui.components.CustomSearchBar
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

@Immutable
data class TaminOptionSheetItem(
    val id: String,
    val label: String,
)

/**
 * Modal sheet for picking one labeled option from a (possibly long) list.
 * When [showSearch] is true, filters [items] offline by [TaminOptionSheetItem.label].
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
