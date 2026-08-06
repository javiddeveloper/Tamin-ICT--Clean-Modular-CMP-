package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.DocType
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_DOCUMENTS
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_INQUIRY
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_SUCCESS
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_VERIFICATION
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.StepperMode
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.UploadedDocument
import com.tamin.taminhamrah.model.addDependent.BranchPR
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

// Shared sample data
private val sampleBranch = BranchPR(
    branchCode = "0201",
    branchName = "شعبه تهران مرکزی",
    workshopCode = "W001",
    workshopName = "کارگاه اصلی"
)

private val sampleRelationships = listOf(
    FamilyRelationshipPR(id = 1, relationCode = "01", relationDesc = "همسر", bailCode = "B1"),
    FamilyRelationshipPR(id = 2, relationCode = "02", relationDesc = "فرزند پسر", bailCode = "B2"),
    FamilyRelationshipPR(id = 3, relationCode = "03", relationDesc = "فرزند دختر", bailCode = "B3")
)

private val sampleWifeRegistry = RegistryDataPR(
    age = 34,
    birthDate = "1369/05/12",
    fatherName = "محمد",
    firstName = "منصوره",
    lastName = "آزادی",
    fullName = "منصوره آزادی",
    nationalId = "0073160997",
    gender = "زن",
    registryConfirmState = "تأیید شده"
)

private val sampleSonRegistry = RegistryDataPR(
    age = 8,
    birthDate = "1396/03/07",
    fatherName = "حسین",
    firstName = "امیرعلی",
    lastName = "آزادی",
    fullName = "امیرعلی آزادی",
    nationalId = "0052213341",
    gender = "مرد",
    registryConfirmState = "تأیید شده"
)

// ─────────────────────────────────────────
// Step 1 – Inquiry
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step1_Empty() {
    PreviewRtlThemeContent {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_INQUIRY,
                familyRelationships = sampleRelationships,
                activeBranches = listOf(sampleBranch),
                selectedBranch = sampleBranch
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step1_Loading() {
    PreviewRtlThemeContent {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_INQUIRY,
                isLoading = true,
                dependentNationalId = "0073160997",
                birthDatePersian = "1369/05/12",
                familyRelationships = sampleRelationships,
                selectedRelationship = sampleRelationships.first()
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step1_InitialLoadError() {
    PreviewRtlThemeContent {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_INQUIRY,
                error = "خطا در برقراری ارتباط با سرور"
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ─────────────────────────────────────────
// Step 2 – Verification
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step2_Wife() {
    PreviewRtlThemeContent {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_VERIFICATION,
                stepperMode = StepperMode.DEFAULT_MODE,
                dependentNationalId = "0073160997",
                birthDatePersian = "1369/05/12",
                familyRelationships = sampleRelationships,
                selectedRelationship = sampleRelationships[0],
                registryData = sampleWifeRegistry,
                activeBranches = listOf(sampleBranch),
                selectedBranch = sampleBranch
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step2_Son() {
    PreviewRtlThemeContent {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_VERIFICATION,
                stepperMode = StepperMode.SON_MODE,
                dependentNationalId = "0052213341",
                birthDatePersian = "1396/03/07",
                familyRelationships = sampleRelationships,
                selectedRelationship = sampleRelationships[1],
                registryData = sampleSonRegistry,
                activeBranches = listOf(sampleBranch),
                selectedBranch = sampleBranch,
                educationCode = "40012",
                universityName = "دبستان شهید مطهری"
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step2_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_VERIFICATION,
                stepperMode = StepperMode.DEFAULT_MODE,
                familyRelationships = sampleRelationships,
                selectedRelationship = sampleRelationships[0],
                registryData = sampleWifeRegistry,
                activeBranches = listOf(sampleBranch),
                selectedBranch = sampleBranch
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ─────────────────────────────────────────
// Step 3 – Document Upload
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step3_Docs() {
    PreviewRtlThemeContent {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_DOCUMENTS,
                familyRelationships = sampleRelationships,
                selectedRelationship = sampleRelationships[0],
                registryData = sampleWifeRegistry,
                requiredDocTypes = listOf(
                    DocType(code = "NIK", title = "کارت ملی"),
                    DocType(code = "SHN", title = "صفحه اول شناسنامه"),
                    DocType(code = "NKH", title = "سند ازدواج")
                ),
                uploadedDocuments = listOf(
                    UploadedDocument(
                        guid = "abc-123",
                        docType = "NIK",
                        fileName = "national_id.jpg"
                    )
                )
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ─────────────────────────────────────────
// Step 4 – Success
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step4_Success() {
    PreviewRtlThemeContent {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_SUCCESS,
                familyRelationships = sampleRelationships,
                selectedRelationship = sampleRelationships[0],
                registryData = sampleWifeRegistry
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AddDependentPreview_Step4_Success_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AddDependentContent(
            state = AddDependentState(
                currentStep = STEP_SUCCESS,
                familyRelationships = sampleRelationships,
                selectedRelationship = sampleRelationships[1],
                registryData = sampleSonRegistry
            ),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
