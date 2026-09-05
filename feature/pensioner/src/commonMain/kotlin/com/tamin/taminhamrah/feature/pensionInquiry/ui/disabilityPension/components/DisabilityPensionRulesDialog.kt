package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.close_content_description
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
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_cross

@Composable
fun DisabilityPensionRulesDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val colors = LocalTaminColors.current
        val headerBrush = Brush.horizontalGradient(colors.profileGradientStops)

        Scaffold(
            modifier = Modifier.fillMaxSize().background(colors.bgPage),
            topBar = {
                TaminTopAppBar(
                    title = stringResource(Res.string.disability_pension_rules_title),
                    background = headerBrush,
                    navigationIcon = {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = stringResource(Res.string.back_content_description),
                            onClick = onDismiss,
                            bordered = true,
                        )
                    },
                    action = {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_tamin_cross),
                            contentDescription = stringResource(Res.string.close_content_description),
                            onClick = onDismiss,
                            bordered = true,
                        )
                    },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
        }
    }
}
