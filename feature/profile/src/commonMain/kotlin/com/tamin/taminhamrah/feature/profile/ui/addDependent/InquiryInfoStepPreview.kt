package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

private val mockRelationships = listOf(
    FamilyRelationshipPR(id = 1, relationCode = "HMR", relationDesc = "همسر", bailCode = "B1"),
    FamilyRelationshipPR(id = 2, relationCode = "FRZ", relationDesc = "فرزند پسر", bailCode = "B2"),
    FamilyRelationshipPR(id = 3, relationCode = "FRD", relationDesc = "فرزند دختر", bailCode = "B3")
)

// ─────────────────────────────────────────
// Empty state (fresh form)
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun InquiryInfoStepPreview_Empty() {
    PreviewRtlThemeContent {
        InquiryInfoStep(
            state = AddDependentState(
                familyRelationships = mockRelationships
            ),
            onIntent = {}
        )
    }
}

// ─────────────────────────────────────────
// Wife – national ID + birthdate filled
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun InquiryInfoStepPreview_Wife_Filled() {
    PreviewRtlThemeContent {
        InquiryInfoStep(
            state = AddDependentState(
                familyRelationships = mockRelationships,
                selectedRelationship = mockRelationships[0],
                dependentNationalId = "0073160997",
                birthDatePersian = "1369/05/12"
            ),
            onIntent = {}
        )
    }
}

// ─────────────────────────────────────────
// Son – loading (inquiry in progress)
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun InquiryInfoStepPreview_Son_Loading() {
    PreviewRtlThemeContent {
        InquiryInfoStep(
            state = AddDependentState(
                familyRelationships = mockRelationships,
                selectedRelationship = mockRelationships[1],
                dependentNationalId = "0052213341",
                birthDatePersian = "1396/03/07",
                isLoading = true
            ),
            onIntent = {}
        )
    }
}

// ─────────────────────────────────────────
// Dark theme – empty
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun InquiryInfoStepPreview_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        InquiryInfoStep(
            state = AddDependentState(
                familyRelationships = mockRelationships,
                dependentNationalId = "0041108876",
                birthDatePersian = "1390/11/20"
            ),
            onIntent = {}
        )
    }
}
