package com.tamin.taminhamrah.feature.addDependent.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentState
import com.tamin.taminhamrah.feature.addDependent.ui.model.FamilyRelationshipPR
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.inquiry_birth_date_placeholder
import taminx.core.core_ui.inquiry_date_picker_title
import taminx.core.core_ui.inquiry_national_id_label
import taminx.core.core_ui.inquiry_relationship_placeholder
import taminx.core.core_ui.inquiry_title

@Composable
fun InquiryInfoStep(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.inquiry_date_picker_title),
            onDismiss = { showDatePicker = false },
            onConfirm = { year, month, day ->
                val dateStr = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
                val timestamp = PersianDateFormatter.toEpochMillis(year, month, day).toString()
                onIntent(AddDependentIntent.OnBirthDateSelected(dateStr, dateStr, timestamp))
                showDatePicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg)
    ) {

        StepSectionTitle(title = stringResource(Res.string.inquiry_title))
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(stringResource(Res.string.inquiry_national_id_label), style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(Spacing.sm))
        NationalIdField(
            value = state.dependentNationalId,
            onValueChange = { onIntent(AddDependentIntent.OnNationalIdChanged(it)) }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        SelectableFieldRow(
            value = state.birthDatePersian,
            placeholder = stringResource(Res.string.inquiry_birth_date_placeholder),
            trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
            onClick = { showDatePicker = true }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        RelationshipDropdown(
            selectedRelationship = state.selectedRelationship,
            onShowPicker = { onIntent(AddDependentIntent.ShowRelationshipPicker) }
        )
    }
}

@Composable
private fun NationalIdField(
    value: String,
    onValueChange: (String) -> Unit
) {
    SegmentedInputField(
        value = value,
        onValueChange = { if (it.length <= 10) onValueChange(it) },
        slotCount = 10,
        leadingIcon = vectorResource(Res.drawable.ic_number),
        keyboardType = KeyboardType.Number
    )
}

@Composable
private fun RelationshipDropdown(
    selectedRelationship: FamilyRelationshipPR?,
    onShowPicker: () -> Unit
) {
    Box {
        SelectableFieldRow(
            value = selectedRelationship?.relationDesc.orEmpty(),
            placeholder = stringResource(Res.string.inquiry_relationship_placeholder),
            trailingIcon = vectorResource(Res.drawable.ic_arrow_down),
            onClick = onShowPicker
        )
    }
}
