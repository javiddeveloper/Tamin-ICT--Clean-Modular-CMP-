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
import androidx.compose.runtime.Immutable
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
import taminx.core.core_ui.contract_rules_sheet_freelance_p1
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item1
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item2
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item3
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item4
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_title
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_item1
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_item2
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_item3
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_title
import taminx.core.core_ui.contract_rules_sheet_housewife_p1
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item1
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item2
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item3
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item4
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_title
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_item1
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_item2
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_item3
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_title
import taminx.core.core_ui.contract_rules_sheet_optional_p1
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item1
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item2
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item3
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item4
import taminx.core.core_ui.contract_rules_sheet_optional_section1_title
import taminx.core.core_ui.contract_rules_sheet_optional_section2_item1
import taminx.core.core_ui.contract_rules_sheet_optional_section2_item2
import taminx.core.core_ui.contract_rules_sheet_optional_section2_item3
import taminx.core.core_ui.contract_rules_sheet_optional_section2_title
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
import taminx.core.core_ui.contract_rules_sheet_title_freelance
import taminx.core.core_ui.contract_rules_sheet_title_housewife
import taminx.core.core_ui.contract_rules_sheet_title_optional
import taminx.core.core_ui.contract_rules_sheet_title_student
import taminx.core.core_ui.contract_rules_sheet_understood

@Immutable
data class ContractRulesSection(
    val title: String,
    val items: List<String>,
)

@Immutable
data class ContractRulesContent(
    val title: String,
    val intro: String,
    val sections: List<ContractRulesSection>,
)

enum class ContractRulesVariant {
    STUDENT,
    FREELANCE,
    HOUSEWIFE,
    OPTIONAL,
}

@Composable
fun contractRulesContent(variant: ContractRulesVariant): ContractRulesContent = when (variant) {
    ContractRulesVariant.STUDENT -> ContractRulesContent(
        title = stringResource(Res.string.contract_rules_sheet_title_student),
        intro = stringResource(Res.string.contract_rules_sheet_student_p1),
        sections = listOf(
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_student_section1_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_student_section1_item1),
                    stringResource(Res.string.contract_rules_sheet_student_section1_item2),
                    stringResource(Res.string.contract_rules_sheet_student_section1_item3),
                    stringResource(Res.string.contract_rules_sheet_student_section1_item4),
                ),
            ),
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_student_section2_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_student_section2_item1),
                    stringResource(Res.string.contract_rules_sheet_student_section2_item2),
                    stringResource(Res.string.contract_rules_sheet_student_section2_item3),
                ),
            ),
        ),
    )
    ContractRulesVariant.FREELANCE -> ContractRulesContent(
        title = stringResource(Res.string.contract_rules_sheet_title_freelance),
        intro = stringResource(Res.string.contract_rules_sheet_freelance_p1),
        sections = listOf(
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_freelance_section1_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_freelance_section1_item1),
                    stringResource(Res.string.contract_rules_sheet_freelance_section1_item2),
                    stringResource(Res.string.contract_rules_sheet_freelance_section1_item3),
                    stringResource(Res.string.contract_rules_sheet_freelance_section1_item4),
                ),
            ),
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_freelance_section2_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_freelance_section2_item1),
                    stringResource(Res.string.contract_rules_sheet_freelance_section2_item2),
                    stringResource(Res.string.contract_rules_sheet_freelance_section2_item3),
                ),
            ),
        ),
    )
    ContractRulesVariant.HOUSEWIFE -> ContractRulesContent(
        title = stringResource(Res.string.contract_rules_sheet_title_housewife),
        intro = stringResource(Res.string.contract_rules_sheet_housewife_p1),
        sections = listOf(
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_housewife_section1_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_housewife_section1_item1),
                    stringResource(Res.string.contract_rules_sheet_housewife_section1_item2),
                    stringResource(Res.string.contract_rules_sheet_housewife_section1_item3),
                    stringResource(Res.string.contract_rules_sheet_housewife_section1_item4),
                ),
            ),
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_housewife_section2_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_housewife_section2_item1),
                    stringResource(Res.string.contract_rules_sheet_housewife_section2_item2),
                    stringResource(Res.string.contract_rules_sheet_housewife_section2_item3),
                ),
            ),
        ),
    )
    ContractRulesVariant.OPTIONAL -> ContractRulesContent(
        title = stringResource(Res.string.contract_rules_sheet_title_optional),
        intro = stringResource(Res.string.contract_rules_sheet_optional_p1),
        sections = listOf(
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_optional_section1_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_optional_section1_item1),
                    stringResource(Res.string.contract_rules_sheet_optional_section1_item2),
                    stringResource(Res.string.contract_rules_sheet_optional_section1_item3),
                    stringResource(Res.string.contract_rules_sheet_optional_section1_item4),
                ),
            ),
            ContractRulesSection(
                title = stringResource(Res.string.contract_rules_sheet_optional_section2_title),
                items = listOf(
                    stringResource(Res.string.contract_rules_sheet_optional_section2_item1),
                    stringResource(Res.string.contract_rules_sheet_optional_section2_item2),
                    stringResource(Res.string.contract_rules_sheet_optional_section2_item3),
                ),
            ),
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractRulesBottomSheet(
    content: ContractRulesContent,
    onDismiss: () -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = content.title,
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
                    text = content.intro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )

                content.sections.forEach { section ->
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                        )
                        section.items.forEach { item ->
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                            )
                        }
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
            content = contractRulesContent(ContractRulesVariant.FREELANCE),
            onDismiss = {},
        )
    }
}
