package com.tamin.taminhamrah.feature.pensionSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorRelationClassifier
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.amount_unknown
import taminx.core.core_ui.ic_person
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.pension_survivor_card_subtitle
import taminx.core.core_ui.pension_survivor_complete_info
import taminx.core.core_ui.pension_survivor_info_completed
import taminx.core.core_ui.pension_survivor_relation_survivor
import taminx.core.core_ui.pension_survivor_survivors_empty
import taminx.core.core_ui.pension_survivor_survivors_progress
import taminx.core.core_ui.pension_survivor_survivors_section_title

@Composable
fun SurvivorsStep(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading && state.survivors.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        state.survivors.isEmpty() -> {
            TaminEmptyState(
                message = stringResource(Res.string.pension_survivor_survivors_empty),
                modifier = modifier.fillMaxSize(),
            )
        }

        else -> {
            val completedCount = state.survivors.count { survivor ->
                state.survivorContactDrafts.containsKey(survivor.nationalId)
            }
            val nowMs = remember { Clock.System.now().toEpochMilliseconds() }

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                item {
                    SurvivorsSectionHeader(
                        completedCount = completedCount,
                        totalCount = state.survivors.size,
                    )
                }
                items(
                    items = state.survivors,
                    key = { it.nationalId },
                ) { survivor ->
                    SurvivorCard(
                        survivor = survivor,
                        isCompleted = state.survivorContactDrafts.containsKey(survivor.nationalId),
                        nowMs = nowMs,
                        onCompleteClick = {
                            onIntent(PensionSurvivorIntent.OpenSurvivor(survivor))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SurvivorsSectionHeader(
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val isComplete = completedCount >= totalCount && totalCount > 0

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.pension_survivor_survivors_section_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        StatusPill(
            text = stringResource(
                Res.string.pension_survivor_survivors_progress,
                completedCount.toString(),
                totalCount.toString(),
            ),
            containerColor = if (isComplete) colors.greenBg else colors.orangeBg,
            contentColor = if (isComplete) colors.greenText else colors.orangeText,
        )
    }
}

@Composable
private fun SurvivorCard(
    survivor: SurvivorDependentPR,
    isCompleted: Boolean,
    nowMs: Long,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val fullName = listOf(survivor.firstName, survivor.lastName)
        .filter(String::isNotBlank)
        .joinToString(" ")
        .ifBlank { stringResource(Res.string.amount_unknown) }
    val relationRes = SurvivorRelationClassifier.relationTitleRes(
        tendencyCode = survivor.tendencyCode,
        genderCode = survivor.genderCode,
    )
    val relationLabel = relationRes?.let { stringResource(it) }
        ?: stringResource(Res.string.pension_survivor_relation_survivor)
    val ageYears = SurvivorRelationClassifier.ageYearsFromBirthDateString(
        dateOfBirth = survivor.dateOfBirth,
        nowMs = nowMs,
    )
    val subtitle = stringResource(
        Res.string.pension_survivor_card_subtitle,
        relationLabel,
        ageYears.toString(),
        survivor.nationalId.ifBlank { stringResource(Res.string.amount_unknown) },
    )

    Column(
        modifier = modifier
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
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.xlarge)
                    .background(colors.hawkesBlue, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_person),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.medium),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
            if (isCompleted) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_check),
                    contentDescription = stringResource(Res.string.pension_survivor_info_completed),
                    tint = colors.greenText,
                    modifier = Modifier.size(IconSize.medium),
                )
            }
        }

        if (!isCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .dashedOutline(colors.blueText, CornerRadius.lg, Thickness.border)
                    .background(colors.hawkesBlue, RoundedCornerShape(CornerRadius.lg))
                    .clickable(onClick = onCompleteClick)
                    .padding(vertical = Spacing.md, horizontal = Spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.pension_survivor_complete_info),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.blueText,
                )
            }
        }
    }
}
