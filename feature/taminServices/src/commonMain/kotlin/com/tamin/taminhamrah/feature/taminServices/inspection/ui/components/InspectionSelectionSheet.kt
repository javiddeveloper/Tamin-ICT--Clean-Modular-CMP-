package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_request_selection_empty

/** One selectable choice inside an [InspectionSelectionSheet]. */
@Immutable
data class InspectionSelectionOption(
    val id: String,
    val title: String,
)

/**
 * Single-select, searchable bottom sheet for the request wizard's code/label picker fields
 * (branch, job title): drag-handle sheet, title, a rounded search field, and a flat list of
 * plain rounded rows — matches the product-supplied design, no radio buttons/borders. The branch
 * and job lists are prefetched up front (see `InspectionViewModel.handleOpenRequestFlow`) and
 * filtering here is purely local/client-side against the already-loaded [options].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InspectionSelectionSheet(
    title: String,
    options: List<InspectionSelectionOption>,
    selectedId: String?,
    onSelect: (InspectionSelectionOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String? = null,
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }

    val filteredOptions = remember(options, query) {
        if (query.isBlank()) options else options.filter { it.title.contains(query, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgPage,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TaminText(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary,
                modifier = Modifier.fillMaxWidth(),
            )

            if (searchPlaceholder != null) {
                CustomSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    placeHolder = searchPlaceholder,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (filteredOptions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    TaminText(
                        text = stringResource(Res.string.inspection_request_selection_empty),
                        color = taminColors.textMuted,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(filteredOptions, key = { it.id }) { option ->
                        InspectionSelectionOptionRow(
                            option = option,
                            selected = option.id == selectedId,
                            onClick = { onSelect(option) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}

@Composable
private fun InspectionSelectionOptionRow(
    option: InspectionSelectionOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val background = if (selected) taminColors.blueBg else taminColors.bgSurface
    val contentColor = if (selected) taminColors.blueText else taminColors.textPrimary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(background, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        TaminText(
            text = option.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
