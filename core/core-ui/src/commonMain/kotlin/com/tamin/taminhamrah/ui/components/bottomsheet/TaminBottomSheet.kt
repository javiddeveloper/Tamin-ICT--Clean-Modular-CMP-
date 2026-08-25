package com.tamin.taminhamrah.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.tamin.taminhamrah.ui.components.InteractiveChoiceChips
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahShapes
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaminBottomSheet(
    config: TaminBottomSheetConfig,
    onDismissRequest: () -> Unit,
    onSubmit: (TaminBottomSheetResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val submitBtnText = config.submitText ?: stringResource(Res.string.bs_submit)

    val selectedIds = remember(config.items) {
        mutableStateListOf<Int>().apply {
            addAll(config.items.filter { it.isSelected }.map { it.id })
        }
    }

    var inputText by remember { mutableStateOf("") }
    var descriptionText by remember(config.description) { mutableStateOf(config.description ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = LocalTaminColors.current.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = config.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.End,
                        color = LocalTaminColors.current.textPrimary,
                        fontWeight = FontWeight(800)
                    ),
                )
                Box(
                    modifier = Modifier.size(34.dp)
                        .clip(TaminHamrahShapes.medium)
                        .background(color = LocalTaminColors.current.divider)
                        .clickable { onDismissRequest() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(Spacing.lg),
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.action_close),
                        tint = LocalTaminColors.current.textSecondary
                    )
                }
            }

            val subtitleText = config.subtitle ?: config.subtitleRes?.let { stringResource(it) }
            if (subtitleText != null) {
                TaminText(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LocalTaminColors.current.textTertiary,
                        textAlign = TextAlign.End
                    ),
                )
            }

            if (config.showWarning && config.warningText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp))
                        .padding(Spacing.md)
                ) {
                    TaminText(
                        text = config.warningText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100)
                    )
                }
            }

            if (config.showSearchInput) {
                CustomSearchBar(
                    query = inputText,
                    onQueryChange = { inputText = it },
                    placeHolder = config.searchInputHint ?: stringResource(Res.string.active_relation_search_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val filteredItems = if (config.showSearchInput && inputText.isNotBlank()) {
                config.items.filter { it.title.contains(inputText, ignoreCase = true) }
            } else {
                config.items
            }

            if (config.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LocalTaminColors.current.blueText,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else if (config.type.showSearch) {
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TaminText(
                            text = stringResource(Res.string.no_items_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalTaminColors.current.textTertiary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        items(filteredItems) { item ->
                            val isSelected = selectedIds.contains(item.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                        if (config.singleSelection) {
                                            selectedIds.clear()
                                            selectedIds.add(item.id)
                                        } else {
                                            if (isSelected) selectedIds.remove(item.id)
                                            else selectedIds.add(item.id)
                                        }
                                    }
                                    .padding(vertical = Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = LocalTaminColors.current.textSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.sm))
                                    TaminText(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LocalTaminColors.current.textPrimary
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = LocalTaminColors.current.blueText,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            } else if (config.items.isEmpty()) {
                // Without this the sheet drew nothing at all — no rows, no explanation — which is
                // indistinguishable from a broken screen.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TaminText(
                        text = stringResource(Res.string.no_items_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalTaminColors.current.textTertiary
                    )
                }
            } else {
                val options = filteredItems.map { it.title }
                val selectedIndices = filteredItems
                    .mapIndexedNotNull { index, item -> if (selectedIds.contains(item.id)) index else null }
                    .toSet()

                InteractiveChoiceChips(
                    options = options,
                    selectedIndices = selectedIndices,
                    onSelectionChanged = { newIndices ->
                        if (config.singleSelection) {
                            val addedIndex = (newIndices - selectedIndices).firstOrNull()
                                ?: newIndices.firstOrNull()
                            selectedIds.clear()
                            if (addedIndex != null && addedIndex in filteredItems.indices) {
                                selectedIds.add(filteredItems[addedIndex].id)
                            }
                        } else {
                            selectedIds.clear()
                            newIndices.forEach { idx ->
                                if (idx in filteredItems.indices) {
                                    selectedIds.add(filteredItems[idx].id)
                                }
                            }
                        }
                    }
                )
            }

            if (config.description != null) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                TaminText(
                    text = stringResource(Res.string.description_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalTaminColors.current.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(TaminHamrahShapes.large)
                        .border(
                            1.dp,
                            LocalTaminColors.current.grey900,
                            TaminHamrahShapes.large
                        ),
                    shape = TaminHamrahShapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = LocalTaminColors.current.textPrimary,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight(500)
                    )
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            if (config.cancelText != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    TaminOutlinedButton(
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = LocalTaminColors.current.textSecondary),
                        text = config.cancelText,
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    )
                    TaminFilledButton(
                        text = submitBtnText,
                        onClick = {
                            onSubmit(
                                TaminBottomSheetResult(
                                    type = config.type,
                                    selectedItemIds = selectedIds.toList(),
                                    text = inputText.takeIf { it.isNotBlank() },
                                    description = descriptionText.takeIf { it.isNotBlank() }
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                TaminFilledButton(
                    enabled = selectedIds.isNotEmpty(),
                    text = submitBtnText,
                    onClick = {
                        onSubmit(
                            TaminBottomSheetResult(
                                type = config.type,
                                selectedItemIds = selectedIds.toList(),
                                text = inputText.takeIf { it.isNotBlank() },
                                description = descriptionText.takeIf { it.isNotBlank() }
                              )
                        )
                    }
                )
            }
        }
    }
}
