package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.relation.DisabilityRelationClassifier
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminCheckbox
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_add_dependent
import taminx.core.core_ui.disability_pension_dependents_collapse
import taminx.core.core_ui.disability_pension_dependents_confirm_error
import taminx.core.core_ui.disability_pension_dependents_confirm_label
import taminx.core.core_ui.disability_pension_dependents_empty
import taminx.core.core_ui.disability_pension_dependents_info_banner
import taminx.core.core_ui.disability_pension_dependents_show_details
import taminx.core.core_ui.disability_pension_refresh_dependents
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_national_code

@Composable
fun DisabilityPensionDependentsStep(
    state: DisabilityPensionUiState,
    onIntent: (DisabilityPensionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        BannerCard(
            message = stringResource(Res.string.disability_pension_dependents_info_banner),
            type = BannerType.Info,
        )

        when {
            state.isDependentsLoading && state.dependents.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xxl),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            state.dependents.isEmpty() -> {
                TaminEmptyState(message = stringResource(Res.string.disability_pension_dependents_empty))
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    state.dependents.forEach { dependent ->
                        DependentCard(
                            dependent = dependent,
                            isExpanded = dependent.nationalId in state.expandedDependentIds,
                            onToggle = {
                                onIntent(DisabilityPensionIntent.DependentCardToggled(dependent.nationalId))
                            },
                        )
                    }
                }
            }
        }

        TaminFilledButton(
            text = stringResource(Res.string.disability_pension_add_dependent),
            onClick = { onIntent(DisabilityPensionIntent.AddDependentClicked) },
            icon = Icons.Filled.Add,
            background = colors.successGradient,
            modifier = Modifier.fillMaxWidth(),
            iconPosition = IconPosition.End,
        )

        TaminOutlinedButton(
            text = stringResource(Res.string.disability_pension_refresh_dependents),
            onClick = { onIntent(DisabilityPensionIntent.RefreshDependentsClicked) },
            icon = Icons.Filled.Refresh,
            modifier = Modifier.fillMaxWidth(),
            contentColor = colors.textSecondary,
            textStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight(900)),
        )

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onIntent(DisabilityPensionIntent.DependentsListConfirmedChanged(!state.isDependentsListConfirmed))
                    },
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminCheckbox(checked = state.isDependentsListConfirmed)
                Text(
                    text = stringResource(Res.string.disability_pension_dependents_confirm_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }

            if (state.showDependentsConfirmationError) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colors.dangerText,
                        modifier = Modifier.size(IconSize.small),
                    )
                    Text(
                        text = stringResource(Res.string.disability_pension_dependents_confirm_error),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.dangerText,
                    )
                }
            }
        }
    }
}

@Composable
private fun DependentCard(
    dependent: DisabilityDependentPR,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val fullName = listOf(dependent.firstName, dependent.lastName)
        .filter(String::isNotBlank)
        .joinToString(" ")
    val relationRes = DisabilityRelationClassifier.relationTitleRes(
        tendencyCode = dependent.tendencyCode,
        genderCode = dependent.genderCode,
    )
    val relationLabel = relationRes?.let { stringResource(it) }
        ?: dependent.tendencyDescription.ifBlank { "-" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgPage)
            .clickable(onClick = onToggle)
            .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = fullName.ifBlank { "-" },
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            StatusPill(
                text = relationLabel,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
            )
        }

        if (isExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(colors.bgSurface, RoundedCornerShape(CornerRadius.lg))
                        .padding(horizontal = Spacing.smd, vertical = Spacing.xs),
                ) {
                    Column {
                        Text(
                            text = stringResource(Res.string.identity_field_national_code),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textMuted,
                        )
                        Text(
                            text = dependent.nationalId.ifBlank { "-" },
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textPrimary,
                        )
                    }

                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(colors.bgSurface, RoundedCornerShape(CornerRadius.lg))
                        .padding(horizontal = Spacing.smd, vertical = Spacing.xs),
                ) {
                    Column {
                        Text(
                            text = stringResource(Res.string.identity_field_birth_date),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textMuted,
                        )
                        Text(
                            text = dependent.dateOfBirth.ifBlank { "-" },
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textPrimary,
                        )
                    }
                }
            }
        }

        Text(
            text = stringResource(
                if (isExpanded) {
                    Res.string.disability_pension_dependents_collapse
                } else {
                    Res.string.disability_pension_dependents_show_details
                },
            ),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
    }
}
