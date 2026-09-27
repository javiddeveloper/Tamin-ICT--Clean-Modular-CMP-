package com.tamin.taminhamrah.feature.historyobjection.ui.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetResult
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahShapes
import com.tamin.taminhamrah.util.containsFoldedWords
import com.tamin.taminhamrah.util.toFoldedWords
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_close
import taminx.core.core_ui.no_items_found

private val SELECTED_CHECK_BADGE_SIZE = 26.dp

/**
 * The 4 step-1 pickers' own bottom sheet — single-select, searchable, with a selected row styled
 * as a filled pill + check badge. Deliberately not the shared `TaminBottomSheet`: that component
 * is used by several other screens with a different selected-row look, and changing it there
 * would have changed everyone's appearance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryObjectionPickerBottomSheet(
    config: TaminBottomSheetConfig,
    onDismissRequest: () -> Unit,
    onSubmit: (TaminBottomSheetResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var inputText by remember { mutableStateOf("") }

    val filteredItems = if (inputText.isBlank()) {
        config.items
    } else {
        val words = inputText.toFoldedWords()
        config.items.filter { it.title.containsFoldedWords(words) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = config.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.End,
                        color = colors.textPrimary,
                        fontWeight = FontWeight(800),
                    ),
                )
            }

            val warningText = config.warningText
            if (config.showWarning && warningText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(colors.orangeBg)
                        .padding(Spacing.md),
                ) {
                    TaminText(
                        text = warningText,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.orangeText,
                    )
                }
            }

            CustomSearchBar(
                query = inputText,
                onQueryChange = { inputText = it },
                placeHolder = config.searchInputHint.orEmpty(),
                containerColor = colors.bgSurface,
                modifier = Modifier.fillMaxWidth(),
            )

            if (config.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = colors.blueText,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    TaminText(
                        text = stringResource(Res.string.no_items_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textTertiary,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    items(filteredItems) { item ->
                        HistoryObjectionPickerRow(
                            item = item,
                            onClick = {
                                onSubmit(
                                    TaminBottomSheetResult(
                                        type = config.type,
                                        selectedItemIds = listOf(item.id)
                                    )
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryObjectionPickerRow(
    item: TaminBottomSheetItem, onClick: () -> Unit
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .then(if (item.isSelected) Modifier.background(colors.blueText.copy(0.1f)) else Modifier)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaminText(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Normal,
            ),
            color = if (item.isSelected) colors.blueText else colors.textPrimary,
        )

        if (item.isSelected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(colors.blueText),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.size(SELECTED_CHECK_BADGE_SIZE))
        }
    }
}
