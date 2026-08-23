package com.tamin.taminhamrah.feature.girlSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorIntent
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorProfileRowPR
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorUiState
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
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

        GirlSurvivorLabeledField(
            label = stringResource(Res.string.girl_survivor_address_label),
            value = state.address,
            onValueChange = { onIntent(GirlSurvivorIntent.AddressChanged(it)) },
            isError = state.fieldErrors.address != null,
            errorText = state.fieldErrors.address,
            singleLine = true,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            GirlSurvivorLabeledField(
                label = stringResource(Res.string.girl_survivor_zipcode_label),
                value = state.zipCode,
                onValueChange = { onIntent(GirlSurvivorIntent.ZipCodeChanged(it)) },
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                isError = state.fieldErrors.zipCode != null,
                errorText = state.fieldErrors.zipCode,
            )
            GirlSurvivorLabeledField(
                label = stringResource(Res.string.girl_survivor_phone_label),
                value = state.phoneNumber,
                onValueChange = { onIntent(GirlSurvivorIntent.PhoneNumberChanged(it)) },
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                isError = state.fieldErrors.phoneNumber != null,
                errorText = state.fieldErrors.phoneNumber,
            )
        }

        GirlSurvivorPensionToggle(
            checked = state.usePensionIdMode,
            onCheckedChange = { onIntent(GirlSurvivorIntent.UsePensionIdModeChanged(it)) },
        )

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
    val shape = RoundedCornerShape(CornerRadius.card)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface)
            .border(1.dp, colors.hawkesBlue, shape)
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

@Composable
private fun GirlSurvivorPensionToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dashedOutline(colors.hawkesBlue, CornerRadius.card, 1.dp)
            .padding(horizontal = Spacing.md, vertical = Spacing.smd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(Res.string.girl_survivor_use_pension_id),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.blueText,
            modifier = Modifier.weight(1f),
        )
        TaminSwitchButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun GirlSurvivorLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            isError = isError,
            supportingText = errorText?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.blueText,
                unfocusedBorderColor = colors.border,
                focusedContainerColor = colors.bgSurface,
                unfocusedContainerColor = colors.bgSurface,
                errorBorderColor = colors.dangerText,
            ),
        )
    }
}
