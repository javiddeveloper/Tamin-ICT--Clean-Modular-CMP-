package com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahShapes

import com.tamin.taminhamrah.feature.healthProfile.ui.components.InteractiveChoiceChips

@Composable
fun HealthBottomSheet(
    config: BottomSheetConfig,
    onDismissRequest: () -> Unit,
    onSubmit: (BottomSheetResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                    modifier = Modifier,
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
                        contentDescription = "Close",
                        tint = LocalTaminColors.current.textSecondary
                    )
                }
            }

            if (config.subtitle != null) {
                TaminText(
                    text = config.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LocalTaminColors.current.textTertiary,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
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
                    placeHolder = config.searchInputHint ?: "جستجو...",
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
                            text = "موردی یافت نشد",
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
            } else if (config.items.isNotEmpty()) {
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
                    text = "توضیحات",
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
                        text = config.submitText,
                        onClick = {
                            onSubmit(
                                BottomSheetResult(
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
                    text = config.submitText,
                    onClick = {
                        onSubmit(
                            BottomSheetResult(
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

@PreviewRtlTheme
@Preview
@Composable
private fun HealthBottomSheetPreview() {
    PreviewRtlThemeContent {
        var showSheet by remember { mutableStateOf(true) }
        if (showSheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = "وضعیت تاهل",
                    subtitle = "در قسمت زیر می\u200Cتوانید وضعیت تأهل خود را انتخاب کنید",
                    type = BottomSheetType.MARITAL_STATUS,
                    singleSelection = true,
                    items = listOf(
                        BottomSheetItem(1, "مجرد"),
                        BottomSheetItem(2, "متاهل", isSelected = true),
                        BottomSheetItem(3, "همسر فوت شده"),
                        BottomSheetItem(4, "رابطه خارج ازدواج")
                    )
                ),
                onDismissRequest = { showSheet = false },
                onSubmit = { showSheet = false }
            )
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun HealthBottomSheetMultiSelectionPreview() {
    PreviewRtlThemeContent {
        var showSheet by remember { mutableStateOf(true) }
        if (showSheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = "آیا سابقه ابتلا به بیماری دارید؟",
                    type = BottomSheetType.ILLNESS_HISTORY,
                    singleSelection = false,
                    items = listOf(
                        BottomSheetItem(1, "فشار خون"),
                        BottomSheetItem(2, "دیابت", isSelected = true),
                        BottomSheetItem(3, "بیماری قلبی"),
                        BottomSheetItem(4, "آسم", isSelected = true)
                    )
                ),
                onDismissRequest = { showSheet = false },
                onSubmit = { showSheet = false }
            )
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun CitySelectionPreview() {
    PreviewRtlThemeContent {
        var showSheet by remember { mutableStateOf(true) }
        if (showSheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = "شهر",
                    type = BottomSheetType.CITY,
                    singleSelection = true,
                    showSearchInput = true,
                    searchInputHint = "جستجوی شهر...",
                    items = listOf(
                        BottomSheetItem(1, "تهران", isSelected = true),
                        BottomSheetItem(2, "مشهد"),
                        BottomSheetItem(3, "اصفهان"),
                        BottomSheetItem(4, "شیراز"),
                        BottomSheetItem(5, "تبریز")
                    )
                ),
                onDismissRequest = { showSheet = false },
                onSubmit = { showSheet = false }
            )
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun DrugAllergyPreview() {
    PreviewRtlThemeContent {
        var showSheet by remember { mutableStateOf(true) }
        if (showSheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = "انتخاب دارو",
                    subtitle = "دارویی که به آن حساسیت دارید را انتخاب کنید.",
                    description = "در دوران حاملگی به این دارو حساسیت داشتم.",
                    type = BottomSheetType.CUSTOM,
                    singleSelection = false,
                    submitText = "افزودن",
                    cancelText = "انصراف",
                    items = listOf(
                        BottomSheetItem(1, "استامینوفن", isSelected = true),
                        BottomSheetItem(2, "پنی‌سیلین"),
                        BottomSheetItem(3, "ایبوپروفن"),
                        BottomSheetItem(4, "آموکسی‌سیلین"),
                        BottomSheetItem(5, "کوتریماکسازول"),
                        BottomSheetItem(6, "سیپروفلوکساسین")
                    )
                ),
                onDismissRequest = { showSheet = false },
                onSubmit = { showSheet = false }
            )
        }
    }
}
