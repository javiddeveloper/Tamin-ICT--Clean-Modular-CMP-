package com.tamin.taminhamrah.feature.contractaffair.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractSearchFilter
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_search_clear
import taminx.core.core_ui.contract_affairs_search_number_hint
import taminx.core.core_ui.contract_affairs_search_number_label
import taminx.core.core_ui.contract_affairs_search_submit
import taminx.core.core_ui.contract_affairs_search_subtitle
import taminx.core.core_ui.contract_affairs_search_title
import taminx.core.core_ui.contract_affairs_search_type_all
import taminx.core.core_ui.contract_affairs_search_type_fraction
import taminx.core.core_ui.contract_affairs_search_type_freelance
import taminx.core.core_ui.contract_affairs_search_type_label
import taminx.core.core_ui.contract_affairs_search_type_optional
import taminx.core.core_ui.ic_tamin_search

/**
 * جستجوی قرارداد — the online search sheet: a شمارهٔ قرارداد field ([TaminStyledTextField], digits
 * only) plus a نوع بیمه single-select list ([ContractSearchFilter]), then جستجو / پاک کردن. Same
 * shape as `pensionInquiry`'s `PayRollSearchSheet`.
 *
 * Edits stay local to the sheet ([contractNumber] / [filter] only seed the initial values) and are
 * pushed to the ViewModel only when جستجو is tapped — dismissing without applying leaves the active
 * search untouched. پاک کردن resets both the local fields and the applied search.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ContractSearchSheet(
    contractNumber: String,
    filter: ContractSearchFilter,
    onApply: (contractNumber: String, filter: ContractSearchFilter) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var number by remember { mutableStateOf(contractNumber) }
    var selectedFilter by remember { mutableStateOf(filter) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                TaminText(
                    text = stringResource(Res.string.contract_affairs_search_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
                TaminText(
                    text = stringResource(Res.string.contract_affairs_search_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }

            TaminStyledTextField(
                value = number,
                onValueChange = { number = it },
                label = stringResource(Res.string.contract_affairs_search_number_label),
                placeholder = stringResource(Res.string.contract_affairs_search_number_hint),
                trailingIcon = vectorResource(Res.drawable.ic_tamin_search),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                inputRestriction = InputRestriction.DigitsOnly,
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TaminText(
                    text = stringResource(Res.string.contract_affairs_search_type_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(start = Spacing.xs),
                )
                ContractSearchFilter.entries.forEach { entry ->
                    ContractFilterOptionRow(
                        label = contractSearchFilterLabel(entry),
                        isSelected = entry == selectedFilter,
                        onClick = { selectedFilter = entry },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminPrimaryButton(
                    background = Brush.linearGradient(listOf(TaminNavy300, TaminNavy900)),
                    text = stringResource(Res.string.contract_affairs_search_submit),
                    onClick = { onApply(number.trim(), selectedFilter) },
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    iconAtStart = true,
                    modifier = Modifier.weight(0.7f),
                )
                TaminOutlinedButton(
                    contentColor = colors.textMuted,
                    containerColor = colors.bgSurface,
                    text = stringResource(Res.string.contract_affairs_search_clear),
                    onClick = {
                        number = ""
                        selectedFilter = ContractSearchFilter.ALL
                        onClear()
                    },
                    modifier = Modifier.weight(0.3f),
                )
            }
        }
    }
}

@Composable
private fun contractSearchFilterLabel(filter: ContractSearchFilter): String = stringResource(
    when (filter) {
        ContractSearchFilter.ALL -> Res.string.contract_affairs_search_type_all
        ContractSearchFilter.FREELANCE -> Res.string.contract_affairs_search_type_freelance
        ContractSearchFilter.OPTIONAL -> Res.string.contract_affairs_search_type_optional
        ContractSearchFilter.FRACTION -> Res.string.contract_affairs_search_type_fraction
    },
)

@Composable
private fun ContractFilterOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val borderColor = if (isSelected) colors.blueText else colors.border
    val bgColor = if (isSelected) colors.blueBg else colors.bgSurface
    val titleColor = if (isSelected) colors.blueText else colors.textPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .border(1.dp, borderColor, RoundedCornerShape(CornerRadius.lg))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = colors.blueText,
                unselectedColor = colors.border,
            ),
        )
        Spacer(Modifier.width(Spacing.sm))
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = titleColor,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractSearchSheetPreviewLight() {
    PreviewRtlThemeContent {
        ContractSearchSheet(
            contractNumber = "",
            filter = ContractSearchFilter.ALL,
            onApply = { _, _ -> },
            onClear = {},
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractSearchSheetPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractSearchSheet(
            contractNumber = "4832222686",
            filter = ContractSearchFilter.OPTIONAL,
            onApply = { _, _ -> },
            onClear = {},
            onDismiss = {},
        )
    }
}
