package com.tamin.taminhamrah.feature.pensionSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.pension_survivor_commitment_body
import taminx.core.core_ui.pension_survivor_commitment_confirm_label
import taminx.core.core_ui.pension_survivor_commitment_title
import taminx.core.core_ui.pension_survivor_rules_banner
import taminx.core.core_ui.pension_survivor_view_rules

@Composable
fun RulesStep(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        BannerCard(
            message = stringResource(Res.string.pension_survivor_rules_banner),
        )

        TaminOutlinedButton(
            text = stringResource(Res.string.pension_survivor_view_rules),
            onClick = { onIntent(PensionSurvivorIntent.ViewRules) },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colors.bgSurface,
                    shape = RoundedCornerShape(CornerRadius.card),
                )
                .border(
                    width = Thickness.border,
                    color = colors.border,
                    shape = RoundedCornerShape(CornerRadius.card),
                )
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            Text(
                text = stringResource(Res.string.pension_survivor_commitment_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )

            Text(
                text = buildCommitmentText(fullName = state.applicantFullName),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Checkbox(
                    checked = state.commitmentAccepted,
                    onCheckedChange = {
                        onIntent(PensionSurvivorIntent.CommitmentChanged(it))
                    },
                    enabled = !state.isProfileLoading,
                )
                Text(
                    text = stringResource(Res.string.pension_survivor_commitment_confirm_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(top = Spacing.md),
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))
    }
}

@Composable
private fun buildCommitmentText(fullName: String) = buildAnnotatedString {
    val colors = LocalTaminColors.current
    val resolvedName = fullName.ifBlank { "..." }
    val template = stringResource(Res.string.pension_survivor_commitment_body)
    val marker = "%1\$s"
    val markerIndex = template.indexOf(marker)

    if (markerIndex == -1) {
        append(template)
        return@buildAnnotatedString
    }

    append(template.substring(0, markerIndex))
    withStyle(
        SpanStyle(
            color = colors.blueText,
            fontWeight = FontWeight.Bold,
        ),
    ) {
        append(resolvedName)
    }
    append(template.substring(markerIndex + marker.length))
}
