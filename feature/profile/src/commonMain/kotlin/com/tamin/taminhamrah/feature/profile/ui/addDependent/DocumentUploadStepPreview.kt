package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.DocType
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.UploadedDocument
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

// ─── Shared doc types ───────────────────────────────────────────────────────

private val wifeDocTypes = listOf(
    DocType(code = "NIK", title = "کارت ملی"),
    DocType(code = "SHN", title = "صفحه اول شناسنامه"),
    DocType(code = "NKH", title = "سند ازدواج")
)

private val sonDocTypes = listOf(
    DocType(code = "NIK", title = "کارت ملی"),
    DocType(code = "SHN", title = "صفحه اول شناسنامه"),
    DocType(code = "TCH", title = "تأییدیه تحصیلی"),
    DocType(code = "BRH", title = "گواهی تجرد (برادر)")
)

private val daughterDocTypes = listOf(
    DocType(code = "NIK", title = "کارت ملی"),
    DocType(code = "SHN", title = "صفحه اول شناسنامه"),
    DocType(code = "TCH", title = "تأییدیه تحصیلی"),
    DocType(code = "CMT", title = "تعهد نامه دختر مجرد")
)

private val wifeRelation = FamilyRelationshipPR(id = 1, relationCode = "HMR", relationDesc = "همسر")
private val sonRelation   = FamilyRelationshipPR(id = 2, relationCode = "FRZ", relationDesc = "فرزند پسر")
private val daughterRelation = FamilyRelationshipPR(id = 3, relationCode = "FRD", relationDesc = "فرزند دختر")

private val wifeRegistry = RegistryDataPR(
    firstName = "منصوره", lastName = "آزادی", nationalId = "0073160997"
)

// ─── Preview 1: Wife – no docs uploaded yet ─────────────────────────────────

@PreviewRtlTheme
@Composable
private fun DocumentUploadStepPreview_Wife_NoDocs() {
    PreviewRtlThemeContent {
        DocumentUploadStep(
            state = AddDependentState(
                currentStep = 3,
                selectedRelationship = wifeRelation,
                registryData = wifeRegistry,
                requiredDocTypes = wifeDocTypes,
                uploadedDocuments = emptyList()
            ),
            onIntent = {}
        )
    }
}

// ─── Preview 2: Wife – 1 of 3 docs uploaded ─────────────────────────────────

@PreviewRtlTheme
@Composable
private fun DocumentUploadStepPreview_Wife_PartialUpload() {
    PreviewRtlThemeContent {
        DocumentUploadStep(
            state = AddDependentState(
                currentStep = 3,
                selectedRelationship = wifeRelation,
                registryData = wifeRegistry,
                requiredDocTypes = wifeDocTypes,
                uploadedDocuments = listOf(
                    UploadedDocument(guid = "a1b2", docType = "NIK", fileName = "national_id_front.jpg")
                )
            ),
            onIntent = {}
        )
    }
}

// ─── Preview 3: Wife – all docs uploaded ────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun DocumentUploadStepPreview_Wife_AllUploaded() {
    PreviewRtlThemeContent {
        DocumentUploadStep(
            state = AddDependentState(
                currentStep = 3,
                selectedRelationship = wifeRelation,
                registryData = wifeRegistry,
                requiredDocTypes = wifeDocTypes,
                uploadedDocuments = listOf(
                    UploadedDocument(guid = "a1", docType = "NIK", fileName = "national_id.jpg"),
                    UploadedDocument(guid = "a2", docType = "SHN", fileName = "shenasname.jpg"),
                    UploadedDocument(guid = "a3", docType = "NKH", fileName = "marriage_cert.pdf")
                )
            ),
            onIntent = {}
        )
    }
}

// ─── Preview 4: Son – partial upload ────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun DocumentUploadStepPreview_Son_PartialUpload() {
    PreviewRtlThemeContent {
        DocumentUploadStep(
            state = AddDependentState(
                currentStep = 3,
                selectedRelationship = sonRelation,
                registryData = RegistryDataPR(firstName = "امیرعلی", lastName = "آزادی", nationalId = "0052213341"),
                requiredDocTypes = sonDocTypes,
                uploadedDocuments = listOf(
                    UploadedDocument(guid = "b1", docType = "NIK", fileName = "kart_melli.jpg"),
                    UploadedDocument(guid = "b2", docType = "TCH", fileName = "tahsiliye.pdf")
                )
            ),
            onIntent = {}
        )
    }
}

// ─── Preview 5: Daughter – 4 doc types ──────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun DocumentUploadStepPreview_Daughter_NoDocs() {
    PreviewRtlThemeContent {
        DocumentUploadStep(
            state = AddDependentState(
                currentStep = 3,
                selectedRelationship = daughterRelation,
                registryData = RegistryDataPR(firstName = "روناک", lastName = "موسوی", nationalId = "0041108876"),
                requiredDocTypes = daughterDocTypes,
                uploadedDocuments = emptyList()
            ),
            onIntent = {}
        )
    }
}

// ─── Preview 6: Dark theme ───────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun DocumentUploadStepPreview_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        DocumentUploadStep(
            state = AddDependentState(
                currentStep = 3,
                selectedRelationship = wifeRelation,
                registryData = wifeRegistry,
                requiredDocTypes = wifeDocTypes,
                uploadedDocuments = listOf(
                    UploadedDocument(guid = "d1", docType = "NIK", fileName = "id.jpg")
                )
            ),
            onIntent = {}
        )
    }
}
