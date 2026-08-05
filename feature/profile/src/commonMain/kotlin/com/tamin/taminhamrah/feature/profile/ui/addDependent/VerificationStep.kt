package com.tamin.taminhamrah.feature.profile.ui.addDependent

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.StepperMode
import com.tamin.taminhamrah.model.addDependent.BranchPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward

/** Static province-capital list — no city lookup endpoint exists yet on [com.tamin.taminhamrah.repository.AddDependentRepository]. */
private val sampleCities = listOf(
    CityPR(cityCode = "01", cityName = "تهران"),
    CityPR(cityCode = "02", cityName = "مشهد"),
    CityPR(cityCode = "03", cityName = "اصفهان"),
    CityPR(cityCode = "04", cityName = "شیراز"),
    CityPR(cityCode = "05", cityName = "تبریز")
)

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
        StepSectionTitle(title = "اطلاعات تبعی جدید")
        Spacer(modifier = Modifier.height(Spacing.md))

        state.registryData?.let { registry ->
            BannerCard(
                message = "اطلاعات هویتی با سازمان ثبت‌احوال تطبیق داده شد.",
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
        StepSectionTitle(title = "اطلاعات تکمیلی")
        Spacer(modifier = Modifier.height(Spacing.smd))

        CityDropdown(
            label = "محل تولد",
            selectedCity = state.selectedCityBirth,
            onCitySelected = { onIntent(AddDependentIntent.OnCityBirthSelected(it)) }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))
        CityDropdown(
            label = "محل صدور",
            selectedCity = state.selectedCityIssuance,
            onCitySelected = { onIntent(AddDependentIntent.OnCityIssuanceSelected(it)) }
        )
        Spacer(modifier = Modifier.height(Spacing.smd))
        BranchDropdown(
            activeBranches = state.activeBranches,
            selectedBranch = state.selectedBranch,
            onBranchSelected = { onIntent(AddDependentIntent.OnBranchSelected(it)) }
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
    gender.equals("MAN", true) || gender == "M" || gender.contains("مرد") -> "مرد"
    gender.equals("WOMAN", true) || gender == "F" || gender.contains("زن") -> "زن"
    else -> gender.ifBlank { "-" }
}

@Composable
private fun CityDropdown(
    label: String,
    selectedCity: CityPR?,
    onCitySelected: (CityPR) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        SelectableFieldRow(
            value = selectedCity?.cityName?.let { "$label: $it" }.orEmpty(),
            placeholder = "$label را انتخاب کنید",
            trailingIcon = vectorResource(Res.drawable.ic_arrow_down),
            onClick = { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sampleCities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city.cityName) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun BranchDropdown(
    activeBranches: List<BranchPR>,
    selectedBranch: BranchPR?,
    onBranchSelected: (BranchPR) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        SelectableFieldRow(
            value = selectedBranch?.let { branch -> branch.branchName.ifBlank { branch.branchCode } }.orEmpty(),
            placeholder = "شعبه تامین اجتماعی را انتخاب کنید",
            trailingIcon = vectorResource(Res.drawable.ic_arrow_down),
            onClick = { if (activeBranches.size > 1) expanded = true },
            enabled = activeBranches.size > 1
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            activeBranches.forEach { branch ->
                DropdownMenuItem(
                    text = { Text(branch.branchName.ifBlank { branch.branchCode }) },
                    onClick = {
                        onBranchSelected(branch)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EducationInquirySection(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    Column {
        StepSectionTitle(title = "استعلام کد تحصیلی")
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
                    text = "استعلام",
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
                message = "محل تحصیل: ${state.universityName}",
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
        StepSectionTitle(title = "تعهدنامه فرزند دختر")
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
                text = "اینجانب عدم ازدواج و عدم اشتغال فرزند دختر خود را تایید مینمایم.",
                color = colors.textPrimary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
