package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractPR
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.legal_representative_contracts_confirm_action
import taminx.core.core_ui.legal_representative_contracts_picker_title
import taminx.core.core_ui.no_items_found

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalRepresentativeContractPickerSheet(
    contracts: List<LegalRepresentativeContractPR>,
    selectedContractRows: List<String>,
    isLoading: Boolean,
    onToggleContractRow: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onConfirm,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.legal_representative_contracts_picker_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            when {
                isLoading -> LegalRepresentativeContractsSkeleton()

                contracts.isEmpty() -> Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.no_items_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = taminColors.textMuted,
                    )
                }

                else -> Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    contracts.forEach { contract ->
                        ContractRow(
                            contract = contract,
                            isSelected = selectedContractRows.contains(contract.contractRow),
                            onClick = { onToggleContractRow(contract.contractRow) },
                        )
                    }
                }
            }

            LoadingButton(
                text = stringResource(Res.string.legal_representative_contracts_confirm_action),
                onClick = onConfirm,
            )
        }
    }
}

@Composable
private fun ContractRow(
    contract: LegalRepresentativeContractPR,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) taminColors.blueBg else taminColors.bgSurface,
                RoundedCornerShape(CornerRadius.xl),
            )
            .border(
                1.dp,
                if (isSelected) taminColors.blueText.copy(0.2f) else taminColors.border,
                RoundedCornerShape(CornerRadius.xl),
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
            colors = CheckboxDefaults.colors(
                checkedColor = taminColors.blueText,
                uncheckedColor = taminColors.border,
            ),
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(
                text = contract.title.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
            )
            NumericText(
                text = contract.contractRow,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted,
            )
        }
    }
}
