package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rules_sheet_page_counter
import taminx.core.core_ui.contract_rules_sheet_student_p1
import taminx.core.core_ui.contract_rules_sheet_student_section1_item1
import taminx.core.core_ui.contract_rules_sheet_student_section1_item2
import taminx.core.core_ui.contract_rules_sheet_student_section1_item3
import taminx.core.core_ui.contract_rules_sheet_student_section1_item4
import taminx.core.core_ui.contract_rules_sheet_student_section1_title
import taminx.core.core_ui.contract_rules_sheet_student_section2_item1
import taminx.core.core_ui.contract_rules_sheet_student_section2_item2
import taminx.core.core_ui.contract_rules_sheet_student_section2_item3
import taminx.core.core_ui.contract_rules_sheet_student_section2_title
import taminx.core.core_ui.contract_rules_sheet_title_student
import taminx.core.core_ui.contract_rules_sheet_understood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractRulesBottomSheet(
    onDismiss: () -> Unit,
    title: String = stringResource(Res.string.contract_rules_sheet_title_student),
    pageCounter: String = stringResource(Res.string.contract_rules_sheet_page_counter),
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.card)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Header Row: Title & Page Counter Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(CornerRadius.chip))
                        .background(colors.bgPage)
                        .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.chip))
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = pageCounter,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            }

            // Scrollable Content Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .clip(cardShape)
                    .background(colors.bgSurface)
                    .border(Thickness.border, colors.border, cardShape)
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = stringResource(Res.string.contract_rules_sheet_student_p1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )

                // Section 1: Conditions
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section1_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section1_item1),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section1_item2),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section1_item3),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section1_item4),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }

                // Section 2: Premium rates & Medical exemptions
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section2_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section2_item1),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section2_item2),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                    Text(
                        text = stringResource(Res.string.contract_rules_sheet_student_section2_item3),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Action Button
            TaminFilledButton(
                text = stringResource(Res.string.contract_rules_sheet_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun ContractRulesBottomSheetContentPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContractRulesBottomSheet(
            onDismiss = {},
        )
    }
}
