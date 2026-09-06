package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_rules_acknowledge
import taminx.core.core_ui.disability_pension_rules_docs_heading
import taminx.core.core_ui.disability_pension_rules_doc_1
import taminx.core.core_ui.disability_pension_rules_doc_2
import taminx.core.core_ui.disability_pension_rules_doc_3
import taminx.core.core_ui.disability_pension_rules_doc_4
import taminx.core.core_ui.disability_pension_rules_doc_5
import taminx.core.core_ui.disability_pension_rules_heading
import taminx.core.core_ui.disability_pension_rules_intro
import taminx.core.core_ui.disability_pension_rules_item_1
import taminx.core.core_ui.disability_pension_rules_item_2
import taminx.core.core_ui.disability_pension_rules_item_3
import taminx.core.core_ui.disability_pension_rules_page_of_total
import taminx.core.core_ui.disability_pension_rules_para_1
import taminx.core.core_ui.disability_pension_rules_para_2
import taminx.core.core_ui.disability_pension_rules_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisabilityPensionRulesDialog(onDismiss: () -> Unit) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = Spacing.md)
                    .size(width = 32.dp, height = 4.dp)
                    .background(colors.border, RoundedCornerShape(50)),
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(Res.string.disability_pension_rules_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = Spacing.lg),
            )

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = stringResource(
                        Res.string.disability_pension_rules_page_of_total,
                        "1".toPersianDigits(),
                        "1".toPersianDigits(),
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier
                        .background(colors.bgSurface, RoundedCornerShape(CornerRadius.full))
                        .padding(horizontal = Spacing.smd, vertical = Spacing.xs)
                        .align(Alignment.End),
                )

                Text(
                    text = stringResource(Res.string.disability_pension_rules_heading),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                )
                Text(
                    text = stringResource(Res.string.disability_pension_rules_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    listOf(
                        Res.string.disability_pension_rules_item_1,
                        Res.string.disability_pension_rules_item_2,
                        Res.string.disability_pension_rules_item_3,
                    ).forEach { item ->
                        Text(
                            text = stringResource(item),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                }

                Text(
                    text = stringResource(Res.string.disability_pension_rules_para_1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
                Text(
                    text = stringResource(Res.string.disability_pension_rules_para_2),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )

                Text(
                    text = stringResource(Res.string.disability_pension_rules_docs_heading),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                )
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    listOf(
                        Res.string.disability_pension_rules_doc_1,
                        Res.string.disability_pension_rules_doc_2,
                        Res.string.disability_pension_rules_doc_3,
                        Res.string.disability_pension_rules_doc_4,
                        Res.string.disability_pension_rules_doc_5,
                    ).forEach { doc ->
                        Text(
                            text = stringResource(doc),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            ) {
                LoadingButton(
                    text = stringResource(Res.string.disability_pension_rules_acknowledge),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
