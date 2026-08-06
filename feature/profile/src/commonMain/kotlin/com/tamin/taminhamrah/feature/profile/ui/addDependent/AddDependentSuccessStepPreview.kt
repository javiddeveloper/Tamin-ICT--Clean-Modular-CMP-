package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

// ─── Shared mock data ───────────────────────────────────────────────────────

private val wifeRelation = FamilyRelationshipPR(id = 1, relationCode = "HMR", relationDesc = "همسر")
private val sonRelation   = FamilyRelationshipPR(id = 2, relationCode = "FRZ", relationDesc = "فرزند پسر")
private val daughterRelation = FamilyRelationshipPR(id = 3, relationCode = "FRD", relationDesc = "فرزند دختر")

private val wifeRegistry = RegistryDataPR(
    age = "34",
    birthDate = "1369/05/12",
    fatherName = "محمد",
    firstName = "منصوره",
    lastName = "آزادی",
    fullName = "منصوره آزادی",
    nationalId = "0073160997",
    gender = "زن"
)

private val sonRegistry = RegistryDataPR(
    age = "8",
    birthDate = "1396/03/07",
    fatherName = "حسین",
    firstName = "امیرعلی",
    lastName = "آزادی",
    fullName = "امیرعلی آزادی",
    nationalId = "0052213341",
    gender = "مرد"
)

private val daughterRegistry = RegistryDataPR(
    age = "22",
    birthDate = "1382/09/15",
    fatherName = "رضا",
    firstName = "روناک",
    lastName = "موسوی",
    fullName = "روناک موسوی",
    nationalId = "0041108876",
    gender = "زن"
)

// ─── Preview 1: Wife success (light) ────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentSuccessStepPreview_Wife() {
    PreviewRtlThemeContent {
        AddDependentSuccessStep(
            state = AddDependentState(
                currentStep = 4,
                selectedRelationship = wifeRelation,
                registryData = wifeRegistry
            ),
        )
    }
}

// ─── Preview 2: Son success (light) ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentSuccessStepPreview_Son() {
    PreviewRtlThemeContent {
        AddDependentSuccessStep(
            state = AddDependentState(
                currentStep = 4,
                selectedRelationship = sonRelation,
                registryData = sonRegistry
            ),
        )
    }
}

// ─── Preview 3: Daughter success (light) ────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentSuccessStepPreview_Daughter() {
    PreviewRtlThemeContent {
        AddDependentSuccessStep(
            state = AddDependentState(
                currentStep = 4,
                selectedRelationship = daughterRelation,
                registryData = daughterRegistry
            ),
        )
    }
}

// ─── Preview 4: No name (anonymous dependent fallback) ───────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentSuccessStepPreview_NoName() {
    PreviewRtlThemeContent {
        AddDependentSuccessStep(
            state = AddDependentState(
                currentStep = 4,
                selectedRelationship = wifeRelation,
                registryData = null  // name falls back to "فرد تبعی"
            ),
        )
    }
}

// ─── Preview 5: Wife success (dark) ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentSuccessStepPreview_Wife_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AddDependentSuccessStep(
            state = AddDependentState(
                currentStep = 4,
                selectedRelationship = wifeRelation,
                registryData = wifeRegistry
            ),
        )
    }
}

// ─── Preview 6: Son success (dark) ──────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun AddDependentSuccessStepPreview_Son_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AddDependentSuccessStep(
            state = AddDependentState(
                currentStep = 4,
                selectedRelationship = sonRelation,
                registryData = sonRegistry
            ),
        )
    }
}
