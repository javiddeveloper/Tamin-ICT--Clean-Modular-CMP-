package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.StepperMode
import com.tamin.taminhamrah.model.addDependent.BranchPR
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

private val sampleBranch = BranchPR(
    branchCode = "0201",
    branchName = "شعبه تهران مرکزی",
    workshopCode = "W001",
    workshopName = "کارگاه اصلی"
)

private val sampleBranches = listOf(
    sampleBranch,
    BranchPR(branchCode = "0312", branchName = "شعبه مشهد", workshopCode = "W002", workshopName = "کارگاه مشهد")
)

private val wifeRelation = FamilyRelationshipPR(id = 1, relationCode = "HMR", relationDesc = "همسر", bailCode = "B1")
private val sonRelation   = FamilyRelationshipPR(id = 2, relationCode = "FRZ", relationDesc = "فرزند پسر", bailCode = "B2")
private val daughterRelation = FamilyRelationshipPR(id = 3, relationCode = "FRD", relationDesc = "فرزند دختر", bailCode = "B3")

private val wifeRegistry = RegistryDataPR(
    age = "34",
    birthDate = "1369/05/12",
    fatherName = "محمد",
    firstName = "منصوره",
    lastName = "آزادی",
    fullName = "منصوره آزادی",
    nationalId = "0073160997",
    gender = "زن",
    registryConfirmState = "تأیید شده"
)

private val sonRegistry = RegistryDataPR(
    age = "8",
    birthDate = "1396/03/07",
    fatherName = "حسین",
    firstName = "امیرعلی",
    lastName = "آزادی",
    fullName = "امیرعلی آزادی",
    nationalId = "0052213341",
    gender = "مرد",
    registryConfirmState = "تأیید شده"
)

private val daughterRegistry = RegistryDataPR(
    age = "22",
    birthDate = "1382/09/15",
    fatherName = "رضا",
    firstName = "روناک",
    lastName = "موسوی",
    fullName = "روناک موسوی",
    nationalId = "0041108876",
    gender = "زن",
    registryConfirmState = "تأیید شده"
)

// ─────────────────────────────────────────
// Wife – DEFAULT_MODE (no extra education/commitment fields)
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun VerificationStepPreview_Wife() {
    PreviewRtlThemeContent {
        VerificationStep(
            state = AddDependentState(
                currentStep = 2,
                stepperMode = StepperMode.DEFAULT_MODE,
                dependentNationalId = "0073160997",
                birthDatePersian = "1369/05/12",
                selectedRelationship = wifeRelation,
                familyRelationships = listOf(wifeRelation, sonRelation, daughterRelation),
                registryData = wifeRegistry,
                activeBranches = sampleBranches,
                selectedBranch = sampleBranch,
                selectedCityBirth = CityPR(cityCode = "01", cityName = "تهران"),
                selectedCityIssuance = CityPR(cityCode = "01", cityName = "تهران")
            ),
            onIntent = {}
        )
    }
}

// ─────────────────────────────────────────
// Son – SON_MODE (shows education code field)
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun VerificationStepPreview_Son() {
    PreviewRtlThemeContent {
        VerificationStep(
            state = AddDependentState(
                currentStep = 2,
                stepperMode = StepperMode.SON_MODE,
                dependentNationalId = "0052213341",
                birthDatePersian = "1396/03/07",
                selectedRelationship = sonRelation,
                familyRelationships = listOf(wifeRelation, sonRelation, daughterRelation),
                registryData = sonRegistry,
                activeBranches = sampleBranches,
                selectedBranch = sampleBranch,
                educationCode = "40012",
                universityName = "دبستان شهید مطهری",
                selectedCityBirth = CityPR(cityCode = "02", cityName = "مشهد"),
                selectedCityIssuance = CityPR(cityCode = "02", cityName = "مشهد")
            ),
            onIntent = {}
        )
    }
}

// ─────────────────────────────────────────
// Daughter – DAUGHTER_MODE (shows commitment checkbox)
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun VerificationStepPreview_Daughter() {
    PreviewRtlThemeContent {
        VerificationStep(
            state = AddDependentState(
                currentStep = 2,
                stepperMode = StepperMode.DAUGHTER_MODE,
                dependentNationalId = "0041108876",
                birthDatePersian = "1382/09/15",
                selectedRelationship = daughterRelation,
                familyRelationships = listOf(wifeRelation, sonRelation, daughterRelation),
                registryData = daughterRegistry,
                activeBranches = sampleBranches,
                selectedBranch = sampleBranch,
                isDaughterCommitmentChecked = true,
                selectedCityBirth = CityPR(cityCode = "03", cityName = "اصفهان"),
                selectedCityIssuance = CityPR(cityCode = "03", cityName = "اصفهان")
            ),
            onIntent = {}
        )
    }
}

// ─────────────────────────────────────────
// Loading – inquiry in progress
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun VerificationStepPreview_Loading() {
    PreviewRtlThemeContent {
        VerificationStep(
            state = AddDependentState(
                currentStep = 2,
                isLoading = true,
                stepperMode = StepperMode.DEFAULT_MODE,
                registryData = wifeRegistry,
                selectedRelationship = wifeRelation,
                activeBranches = sampleBranches,
                selectedBranch = sampleBranch
            ),
            onIntent = {}
        )
    }
}

// ─────────────────────────────────────────
// Dark theme – Wife
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun VerificationStepPreview_Wife_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        VerificationStep(
            state = AddDependentState(
                currentStep = 2,
                stepperMode = StepperMode.DEFAULT_MODE,
                selectedRelationship = wifeRelation,
                familyRelationships = listOf(wifeRelation, sonRelation, daughterRelation),
                registryData = wifeRegistry,
                activeBranches = sampleBranches,
                selectedBranch = sampleBranch
            ),
            onIntent = {}
        )
    }
}
