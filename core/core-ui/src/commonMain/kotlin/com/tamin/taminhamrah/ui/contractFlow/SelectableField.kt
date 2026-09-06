package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableOptionSheet
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.picker_loading
import taminx.core.core_ui.picker_select_hint
import taminx.core.core_ui.picker_select_previous_first
import taminx.core.core_ui.retry

/**
 * A labelled field that opens a searchable sheet to choose one of [options].
 *
 * Built from [PickerRow] and [TaminSearchableOptionSheet] so it reads as the same control as every other
 * chooser in the app, and so a failed lookup can report itself the way a bad input does — through
 * `PickerRow`'s error border rather than by blanking the screen behind it.
 *
 * Pass [errorMessage] when the load behind this field failed. [onRetry], when given, turns the
 * message into a tappable retry so a network blip is not a dead end.
 */
@Composable
fun <T> SelectableField(
    label: String,
    options: List<T>,
    selectedCode: String,
    selectedName: String,
    optionCode: (T) -> String,
    optionName: (T) -> String,
    isLoading: Boolean,
    enabled: Boolean = true,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetry: (() -> Unit)? = null,
    sheetType: TaminBottomSheetType = TaminBottomSheetType.CUSTOM,
    showSearch: Boolean = true,
) {
    val colors = LocalTaminColors.current
    var sheetOpen by remember { mutableStateOf(false) }

    val placeholder = when {
        isLoading -> stringResource(Res.string.picker_loading)
        !enabled -> stringResource(Res.string.picker_select_previous_first)
        else -> stringResource(Res.string.picker_select_hint)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
        )

        PickerRow(
            text = selectedName.ifBlank { placeholder },
            isPlaceholder = selectedName.isBlank(),
            isError = errorMessage != null,
            showChevron = false,
            onClick = { if (enabled) sheetOpen = true },
        )

        if (errorMessage != null) {
            FieldError(message = errorMessage, onRetry = onRetry)
        }
    }

    if (sheetOpen) {
        TaminSearchableOptionSheet(
            title = label,
            items = options,
            itemLabel = optionName,
            itemKey = { optionCode(it) },
            showSearch = showSearch,
            isLoading = isLoading,
            onSelect = { selectedOption ->
                onSelected(selectedOption)
                sheetOpen = false
            },
            onDismiss = { sheetOpen = false },
        )
    }
}

/** The failure line under a field, with the retry the caller allows. */
@Composable
private fun FieldError(
    message: String,
    onRetry: (() -> Unit)?,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = colors.dangerText,
            modifier = Modifier.weight(1f),
        )
        if (onRetry != null) {
            Text(
                text = stringResource(Res.string.retry),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.blueText,
                modifier = Modifier.clickable(onClick = onRetry),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun SelectableFieldPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.background(LocalTaminColors.current.bgPage)) {
            Column(
                modifier = Modifier.padding(Spacing.lg).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                SelectableField(
                    label = "استان",
                    options = listOf("تهران", "مشهد", "اصفهان"),
                    selectedCode = "تهران",
                    selectedName = "تهران",
                    optionCode = { it },
                    optionName = { it },
                    isLoading = false,
                    onSelected = {},
                )
                SelectableField(
                    label = "شهر",
                    options = emptyList<String>(),
                    selectedCode = "",
                    selectedName = "",
                    optionCode = { it },
                    optionName = { it },
                    isLoading = true,
                    onSelected = {},
                )
                SelectableField(
                    label = "شعبه",
                    options = emptyList<String>(),
                    selectedCode = "",
                    selectedName = "",
                    optionCode = { it },
                    optionName = { it },
                    isLoading = false,
                    enabled = false,
                    onSelected = {},
                )
                SelectableField(
                    label = "استان",
                    options = emptyList<String>(),
                    selectedCode = "",
                    selectedName = "",
                    optionCode = { it },
                    optionName = { it },
                    isLoading = false,
                    onSelected = {},
                    errorMessage = "دریافت استان‌ها ناموفق بود",
                    onRetry = {},
                )
            }
        }
    }
}
