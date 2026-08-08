package com.tamin.taminhamrah.feature.addDependent.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentState
import com.tamin.taminhamrah.feature.addDependent.ui.contract.StepperMode
import com.tamin.taminhamrah.feature.addDependent.ui.model.BranchPR
import com.tamin.taminhamrah.feature.addDependent.ui.model.RegistryDataPR
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.verify_banner_success
import taminx.core.core_ui.verify_birth_place_label
import taminx.core.core_ui.verify_branch_placeholder
import taminx.core.core_ui.verify_daughter_commitment_text
import taminx.core.core_ui.verify_daughter_commitment_title
import taminx.core.core_ui.verify_education_inquiry_title
import taminx.core.core_ui.verify_education_submit
import taminx.core.core_ui.verify_issue_place_label
import taminx.core.core_ui.verify_section_additional_info
import taminx.core.core_ui.verify_section_new_info
import taminx.core.core_ui.verify_university_label

@Composable
fun VerificationStep(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg)
    ) {
        StepSectionTitle(title = stringResource(Res.string.verify_section_new_info))
        Spacer(modifier = Modifier.height(Spacing.md))

        state.registryData?.let { registry ->
            BannerCard(
                message = stringResource(Res.string.verify_banner_success),
                type = BannerType.Success
            )
            Spacer(modifier = Modifier.height(Spacing.smd))
            RegistryDataGrid(
                registry = registry,
                relationDesc = state.selectedRelationship?.relationDesc.orEmpty(),
                nationalId = state.dependentNationalId
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))
        StepSectionTitle(title = stringResource(Res.string.verify_section_additional_info))
        Spacer(modifier = Modifier.height(Spacing.smd))

        CityDropdown(
            label = stringResource(Res.string.verify_birth_place_label),
            selectedCity = state.selectedCityBirth,
            onShowPicker = { onIntent(AddDependentIntent.ShowCityBirthPicker) }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))
        CityDropdown(
            label = stringResource(Res.string.verify_issue_place_label),
            selectedCity = state.selectedCityIssuance,
            onShowPicker = { onIntent(AddDependentIntent.ShowCityIssuancePicker) }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))
        BranchDropdown(
            selectedBranch = state.selectedBranch,
            onShowPicker = { onIntent(AddDependentIntent.ShowBranchPicker) },
            enabled = state.activeBranches.isNotEmpty()
        )

        when (state.stepperMode) {
            StepperMode.SON_MODE -> {
                Spacer(modifier = Modifier.height(Spacing.lg))
                EducationInquirySection(state = state, onIntent = onIntent)
            }
            StepperMode.DAUGHTER_MODE -> {
                Spacer(modifier = Modifier.height(Spacing.lg))
                DaughterCommitmentSection(state = state, onIntent = onIntent)
            }
            StepperMode.DEFAULT_MODE -> Unit
        }
    }
}

@Composable
private fun RegistryDataGrid(
    registry: RegistryDataPR,
    relationDesc: String,
    nationalId: String
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg))
            .padding(vertical = Spacing.xs)
    ) {
        RegistryGridRow("نام" to registry.firstName, "نام خانوادگی" to registry.lastName)
        TaminDivider(modifier = Modifier.padding(horizontal = Spacing.sm))
        RegistryGridRow("نام پدر" to registry.fatherName, "سن" to "${registry.age} سال")
        TaminDivider(modifier = Modifier.padding(horizontal = Spacing.sm))
        RegistryGridRow("جنسیت" to formatGender(registry.gender), "نسبت" to relationDesc)
        TaminDivider(modifier = Modifier.padding(horizontal = Spacing.sm))
        RegistryGridRow("کد ملی" to nationalId, "تاریخ تولد" to registry.birthDate)
    }
}

@Composable
private fun RegistryGridRow(right: Pair<String, String>, left: Pair<String, String>) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm)) {
        RegistryFieldCell(label = right.first, value = right.second, modifier = Modifier.weight(1f))
        RegistryFieldCell(label = left.first, value = left.second, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun RegistryFieldCell(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.padding(horizontal = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        Text(
            text = value.ifBlank { "-" },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
    }
}

private fun formatGender(gender: String): String = when {
    gender.equals("MAN", true) || gender == "1" || gender == "M" || gender.contains("مرد") -> "مرد"
    gender.equals("WOMAN", true) || gender == "2" || gender == "F" || gender.contains("زن") -> "زن"
    else -> gender.ifBlank { "-" }
}

@Composable
private fun CityDropdown(
    label: String,
    selectedCity: CityPR?,
    onShowPicker: () -> Unit
) {
    Box {
        SelectableFieldRow(
            value = selectedCity?.cityName?.let { "$label: $it" }.orEmpty(),
            placeholder = label,
            trailingIcon = vectorResource(Res.drawable.ic_arrow_down),
            onClick = { onShowPicker() }
        )
    }
}

@Composable
private fun BranchDropdown(
    selectedBranch: BranchPR?,
    onShowPicker: () -> Unit,
    enabled: Boolean
) {
    Box {
        SelectableFieldRow(
            value = selectedBranch?.let { branch -> branch.branchName.ifBlank { branch.branchCode } }.orEmpty(),
            placeholder = stringResource(Res.string.verify_branch_placeholder),
            trailingIcon = vectorResource(Res.drawable.ic_arrow_down),
            onClick = { if (enabled) onShowPicker() },
            enabled = enabled
        )
    }
}

@Composable
private fun EducationInquirySection(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    Column {
        StepSectionTitle(title = stringResource(Res.string.verify_education_inquiry_title))
        Spacer(modifier = Modifier.height(Spacing.smd))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.smd)) {
            Box(
                modifier = Modifier
                    .height(54.dp)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(colors.buttonGradient)
                    .clickable { onIntent(AddDependentIntent.SubmitInquiryEducation) }
                    .padding(horizontal = Spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.verify_education_submit),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedTextField(
                value = state.educationCode,
                onValueChange = { onIntent(AddDependentIntent.OnEducationCodeChanged(it)) },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                singleLine = true,
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.blueText,
                    unfocusedBorderColor = colors.border
                )
            )
        }
        if (state.universityName.isNotBlank()) {
            Spacer(modifier = Modifier.height(Spacing.smd))
            BannerCard(
                message = stringResource(Res.string.verify_university_label, state.universityName),
                type = BannerType.Success
            )
        }
    }
}

@Composable
private fun DaughterCommitmentSection(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    Column {
        StepSectionTitle(title = stringResource(Res.string.verify_daughter_commitment_title))
        Spacer(modifier = Modifier.height(Spacing.smd))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.bgSurface)
                .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg))
                .clickable {
                    onIntent(AddDependentIntent.OnDaughterCommitmentToggled(!state.isDaughterCommitmentChecked))
                }
                .padding(Spacing.md)
        ) {
            Checkbox(
                checked = state.isDaughterCommitmentChecked,
                onCheckedChange = { onIntent(AddDependentIntent.OnDaughterCommitmentToggled(it)) },
                colors = CheckboxDefaults.colors(checkedColor = colors.blueText)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = stringResource(Res.string.verify_daughter_commitment_text),
                color = colors.textPrimary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
