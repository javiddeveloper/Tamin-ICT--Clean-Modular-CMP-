package com.tamin.taminhamrah.feature.girlSurvivor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorFieldErrors
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorIntent
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorProfileRowPR
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorUiState
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.components.taminSurface
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.girl_survivor_address_label
import taminx.core.core_ui.girl_survivor_deceased_national_id_label
import taminx.core.core_ui.girl_survivor_deceased_pension_id_label
import taminx.core.core_ui.girl_survivor_phone_label
import taminx.core.core_ui.girl_survivor_use_pension_id
import taminx.core.core_ui.girl_survivor_zipcode_label
import taminx.core.core_ui.ic_number

@Composable
fun GirlSurvivorDetailsStep(
    state: GirlSurvivorUiState,
    onIntent: (GirlSurvivorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        GirlSurvivorProfileCard(rows = state.profileRows)

        OutlinedTextField(
            value = state.address,
            onValueChange = { onIntent(GirlSurvivorIntent.AddressChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.girl_survivor_address_label)) },
            placeholder = { Text(stringResource(Res.string.girl_survivor_address_label)) },
            isError = state.fieldErrors.address != null,
            supportingText = state.fieldErrors.address?.let { { Text(it) } },
            minLines = 2,
            singleLine = false,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            OutlinedTextField(
                value = state.zipCode,
                onValueChange = { onIntent(GirlSurvivorIntent.ZipCodeChanged(it)) },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(Res.string.girl_survivor_zipcode_label)) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = state.fieldErrors.zipCode != null,
                supportingText = state.fieldErrors.zipCode?.let { { Text(it) } },
            )
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { onIntent(GirlSurvivorIntent.PhoneNumberChanged(it)) },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(Res.string.girl_survivor_phone_label)) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = state.fieldErrors.phoneNumber != null,
                supportingText = state.fieldErrors.phoneNumber?.let { { Text(it) } },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.girl_survivor_use_pension_id),
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.usePensionIdMode) {
                    LocalTaminColors.current.blueText
                } else {
                    LocalTaminColors.current.textMuted
                },
            )
            TaminSwitchButton(
                checked = state.usePensionIdMode,
                onCheckedChange = { onIntent(GirlSurvivorIntent.UsePensionIdModeChanged(it)) },
            )
        }

        if (state.usePensionIdMode) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = stringResource(Res.string.girl_survivor_deceased_pension_id_label),
                    style = MaterialTheme.typography.bodyMedium,
                )
                SegmentedInputField(
                    value = state.deceasedPensionId,
                    onValueChange = { onIntent(GirlSurvivorIntent.DeceasedPensionIdChanged(it)) },
                    slotCount = 10,
                    leadingIcon = vectorResource(Res.drawable.ic_number),
                    keyboardType = KeyboardType.Number,
                )
                state.fieldErrors.deceasedPensionId?.let { error ->
                    Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = stringResource(Res.string.girl_survivor_deceased_national_id_label),
                    style = MaterialTheme.typography.bodyMedium,
                )
                SegmentedInputField(
                    value = state.deceasedNationalCode,
                    onValueChange = { onIntent(GirlSurvivorIntent.DeceasedNationalCodeChanged(it)) },
                    slotCount = 10,
                    leadingIcon = vectorResource(Res.drawable.ic_number),
                    keyboardType = KeyboardType.Number,
                )
                state.fieldErrors.deceasedNationalCode?.let { error ->
                    Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun GirlSurvivorProfileCard(
    rows: ImmutableList<GirlSurvivorProfileRowPR>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .padding(Spacing.md),
    ) {
        rows.forEachIndexed { index, row ->
            DetailRow(
                label = row.label,
                value = row.value,
                numeric = row.label != "نام و نام خانوادگی" && row.label != "نام پدر",
            )
            if (index < rows.lastIndex) {
                HorizontalDivider(color = colors.border.copy(alpha = 0.5f))
            }
        }
    }
}
