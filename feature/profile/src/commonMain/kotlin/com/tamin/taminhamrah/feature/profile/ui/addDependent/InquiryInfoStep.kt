package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_calendar

@Composable
fun InquiryInfoStep(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        TaminJalaliDatePicker(
            title = "انتخاب تاریخ تولد",
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

        StepSectionTitle(title = "دریافت اطلاعات از ثبت احوال")
        Spacer(modifier = Modifier.height(Spacing.md))
        Text("کد ملی" , style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(Spacing.sm))
        NationalIdField(
            value = state.dependentNationalId,
            onValueChange = { onIntent(AddDependentIntent.OnNationalIdChanged(it)) }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        SelectableFieldRow(
            value = state.birthDatePersian,
            placeholder = "تاریخ تولد را انتخاب کنید",
            trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
            onClick = { showDatePicker = true }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        RelationshipDropdown(
            relationships = state.familyRelationships,
            selectedRelationship = state.selectedRelationship,
            onRelationshipSelected = { onIntent(AddDependentIntent.OnRelationshipSelected(it)) }
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
    relationships: List<FamilyRelationshipPR>,
    selectedRelationship: FamilyRelationshipPR?,
    onRelationshipSelected: (FamilyRelationshipPR) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        SelectableFieldRow(
            value = selectedRelationship?.relationDesc.orEmpty(),
            placeholder = "نسبت خانوادگی را انتخاب کنید",
            trailingIcon = vectorResource(Res.drawable.ic_arrow_down),
            onClick = { if (relationships.isNotEmpty()) expanded = true },
            enabled = relationships.isNotEmpty()
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            relationships.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.relationDesc.orEmpty()) },
                    onClick = {
                        onRelationshipSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
fun InquiryInfoStepPreview(){

    InquiryInfoStepPreview()
}
