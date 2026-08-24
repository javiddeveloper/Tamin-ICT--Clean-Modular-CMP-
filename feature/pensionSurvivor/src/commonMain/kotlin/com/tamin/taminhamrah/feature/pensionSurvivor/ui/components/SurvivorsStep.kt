package com.tamin.taminhamrah.feature.pensionSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.amount_unknown
import taminx.core.core_ui.girl_survivor_label_birth_date
import taminx.core.core_ui.girl_survivor_label_father_name
import taminx.core.core_ui.girl_survivor_label_full_name
import taminx.core.core_ui.girl_survivor_label_insurance_id
import taminx.core.core_ui.pension_survivor_survivors_empty
import taminx.core.core_ui.verify_label_national_id

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
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                items(
                    items = state.survivors,
                    key = { it.nationalId },
                ) { survivor ->
                    SurvivorCard(
                        survivor = survivor,
                        onClick = {
                            onIntent(PensionSurvivorIntent.OpenSurvivor(survivor))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SurvivorCard(
    survivor: SurvivorDependentPR,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rows = buildRows(survivor)

    Box(
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
            .clickable(onClick = onClick)
            .padding(Spacing.md),
    ) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            rows.forEachIndexed { index, row ->
                DetailRow(
                    label = row.label,
                    value = row.value,
                    numeric = row.numeric,
                )
                if (index < rows.lastIndex) {
                    TaminDivider()
                }
            }
        }
    }
}

@Composable
private fun buildRows(survivor: SurvivorDependentPR): List<SurvivorRow> {
    val birthDate = survivor.dateOfBirth
        .toLongOrNull()
        ?.let(PersianDateFormatter::formatTimestamp)
        .orEmpty()

    return listOf(
        SurvivorRow(
            label = stringResource(Res.string.girl_survivor_label_full_name),
            value = listOf(survivor.firstName, survivor.lastName)
                .filter(String::isNotBlank)
                .joinToString(" ")
                .ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
        SurvivorRow(
            label = stringResource(Res.string.verify_label_national_id),
            value = survivor.nationalId.ifBlank { stringResource(Res.string.amount_unknown) },
        ),
        SurvivorRow(
            label = stringResource(Res.string.girl_survivor_label_father_name),
            value = survivor.fatherName.ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
        SurvivorRow(
            label = stringResource(Res.string.girl_survivor_label_insurance_id),
            value = survivor.insuranceId.ifBlank { stringResource(Res.string.amount_unknown) },
        ),
        SurvivorRow(
            label = stringResource(Res.string.girl_survivor_label_birth_date),
            value = birthDate.ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
    )
}

private data class SurvivorRow(
    val label: String,
    val value: String,
    val numeric: Boolean = true,
)
