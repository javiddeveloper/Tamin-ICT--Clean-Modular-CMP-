package com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class SurvivorRelationKind {
    Kid,
    AdultSon,
    AdultDaughter,
    Parent,
    Partner,
    Unknown,
}

enum class SurvivorDocumentType(val code: String) {
    IdFirstPage("03"),
    SpouseIdPage("04"),
    StudyCertificate("09"),
    HusbandMarriagePage("10"),
    WifeMarriagePage("11"),
    MarriageTypePage("12"),
}

enum class SurvivorDependencyType(val code: String) {
    Spouse("02"),
    Son("03"),
    Daughter("04"),
    Parent("06"),
}

data class SurvivorRelationClassification(
    val kind: SurvivorRelationKind,
    val dependencyType: SurvivorDependencyType?,
)

data class SharedDeceasedDocument(
    val documentTypeCode: String,
    val guid: String,
)

fun SurvivorRelationKind.requiredDocuments(): ImmutableList<SurvivorDocumentType> {
    // Legacy getNeededImagesUpload: always 03; add 04 unless IS_KID; then type extras.
    val docs = mutableListOf(SurvivorDocumentType.IdFirstPage)
    if (this != SurvivorRelationKind.Kid) {
        docs.add(SurvivorDocumentType.SpouseIdPage)
    }
    when (this) {
        SurvivorRelationKind.AdultSon -> docs.add(SurvivorDocumentType.StudyCertificate)
        SurvivorRelationKind.Partner -> {
            docs.add(SurvivorDocumentType.WifeMarriagePage)
            docs.add(SurvivorDocumentType.HusbandMarriagePage)
            docs.add(SurvivorDocumentType.MarriageTypePage)
        }
        SurvivorRelationKind.Kid,
        SurvivorRelationKind.AdultDaughter,
        SurvivorRelationKind.Parent,
        SurvivorRelationKind.Unknown -> Unit
    }
    return docs.toImmutableList()
}

private fun List<SurvivorDocumentType>.toImmutableList(): ImmutableList<SurvivorDocumentType> =
    persistentListOf(*toTypedArray())
