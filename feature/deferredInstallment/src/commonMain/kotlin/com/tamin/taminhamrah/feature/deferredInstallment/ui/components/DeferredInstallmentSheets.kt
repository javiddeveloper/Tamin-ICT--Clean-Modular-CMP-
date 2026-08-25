package com.tamin.taminhamrah.feature.deferredInstallment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentOptionUi
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_search

private val SheetCorner = 28.dp
private val OptionRowMinHeight = 64.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeferredInstallmentOptionSheet(
    title: String,
    options: ImmutableList<DeferredInstallmentOptionUi>,
    selectedId: String?,
    onSelect: (DeferredInstallmentOptionUi) -> Unit,
    onDismiss: () -> Unit,
    subtitle: String? = null,
) {
    val colors = LocalTaminColors.current
    var pendingId by remember { mutableStateOf(selectedId) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = SheetCorner, topEnd = SheetCorner),
        contentWindowInsets = { WindowInsets(0) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                options.forEach { option ->
                    DeferredInstallmentOptionRow(
                        option = option,
                        isSelected = option.id == pendingId,
                        onClick = { pendingId = option.id },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.lg))
            TaminFilledButton(
                text = stringResource(Res.string.action_confirm),
                onClick = {
                    options.find { it.id == pendingId }?.let(onSelect)
                },
                enabled = pendingId != null,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeferredInstallmentBankSheet(
    title: String,
    subtitle: String,
    searchPlaceholder: String,
    query: String,
    options: ImmutableList<DeferredInstallmentOptionUi>,
    selectedId: String?,
    isLoading: Boolean,
    onQueryChanged: (String) -> Unit,
    onSelect: (DeferredInstallmentOptionUi) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    var pendingId by remember { mutableStateOf(selectedId) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = SheetCorner, topEnd = SheetCorner),
        contentWindowInsets = { WindowInsets(0) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.md)
                .heightIn(max = 520.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(colors.bgSurface)
                    .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.lg))
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(IconSize.small),
                )
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
                    cursorBrush = SolidColor(colors.blueText),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                text = searchPlaceholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted,
                            )
                        }
                        inner()
                    },
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            if (isLoading) {
                DeferredInstallmentBankListShimmer(
                    modifier = Modifier.weight(1f, fill = false),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(options, key = { it.id }) { option ->
                        DeferredInstallmentOptionRow(
                            option = option,
                            isSelected = option.id == pendingId,
                            onClick = { pendingId = option.id },
                        )
                    }
                }
            }
            Spacer(Modifier.height(Spacing.lg))
            TaminFilledButton(
                text = stringResource(Res.string.action_confirm),
                onClick = {
                    options.find { it.id == pendingId }?.let(onSelect)
                },
                enabled = pendingId != null,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DeferredInstallmentOptionRow(
    option: DeferredInstallmentOptionUi,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val backgroundColor = if (isSelected) colors.blueBg else colors.bgSurface
    val borderColor = if (isSelected) colors.hawkesBlue else colors.border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OptionRowMinHeight)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(backgroundColor)
            .border(Thickness.border, borderColor, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
            )
            if (option.subtitle != null) {
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
        }
        if (isSelected) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.small),
            )
        }
    }
}
