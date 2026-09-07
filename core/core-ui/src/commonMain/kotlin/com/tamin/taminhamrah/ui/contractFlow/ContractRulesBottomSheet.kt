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
import taminx.core.core_ui.contract_rules_sheet_understood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractRulesBottomSheet(
    rulesCopy: ContractRulesCopy,
    onDismiss: () -> Unit,
    pageCounter: String = stringResource(Res.string.contract_rules_sheet_page_counter),
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.card)
    val title = stringResource(rulesCopy.titleRes)
    val intro = stringResource(rulesCopy.introRes)
    val section1Title = stringResource(rulesCopy.section1TitleRes)
    val section1Items = rulesCopy.section1ItemRes.map { stringResource(it) }
    val section2Title = stringResource(rulesCopy.section2TitleRes)
    val section2Items = rulesCopy.section2ItemRes.map { stringResource(it) }

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
                    text = intro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = section1Title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    section1Items.forEach { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = section2Title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    section2Items.forEach { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            TaminFilledButton(
                text = stringResource(Res.string.contract_rules_sheet_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun ContractRulesBottomSheetContentPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContractRulesBottomSheet(
            rulesCopy = ContractRulesCopies.Student,
            onDismiss = {},
        )
    }
}
