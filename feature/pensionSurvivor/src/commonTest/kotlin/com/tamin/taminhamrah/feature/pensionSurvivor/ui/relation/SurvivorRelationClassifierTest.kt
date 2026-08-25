package com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SurvivorRelationClassifierTest {

    @Test
    fun partnerTendency_mapsToPartnerAndSpouseDependency() {
        val result = SurvivorRelationClassifier.classify(
            tendencyCode = "100",
            genderCode = "02",
            ageYears = 40,
        )
        assertEquals(SurvivorRelationKind.Partner, result.kind)
        assertEquals(SurvivorDependencyType.Spouse, result.dependencyType)
        assertEquals(
            listOf(
                SurvivorDocumentType.IdFirstPage,
                SurvivorDocumentType.SpouseIdPage,
                SurvivorDocumentType.WifeMarriagePage,
                SurvivorDocumentType.HusbandMarriagePage,
                SurvivorDocumentType.MarriageTypePage,
            ),
            result.kind.requiredDocuments(),
        )
    }

    @Test
    fun sonUnder19_isKidWithSonDependency() {
        val result = SurvivorRelationClassifier.classify(
            tendencyCode = "101",
            genderCode = "01",
            ageYears = 14,
        )
        assertEquals(SurvivorRelationKind.Kid, result.kind)
        assertEquals(SurvivorDependencyType.Son, result.dependencyType)
        assertEquals(listOf(SurvivorDocumentType.IdFirstPage), result.kind.requiredDocuments())
    }

    @Test
    fun sonAdult_requiresStudyCertificate() {
        val result = SurvivorRelationClassifier.classify(
            tendencyCode = "101",
            genderCode = "01",
            ageYears = 19,
        )
        assertEquals(SurvivorRelationKind.AdultSon, result.kind)
        assertEquals(
            listOf(
                SurvivorDocumentType.IdFirstPage,
                SurvivorDocumentType.SpouseIdPage,
                SurvivorDocumentType.StudyCertificate,
            ),
            result.kind.requiredDocuments(),
        )
    }

    @Test
    fun daughterAgeThreshold_isSixteen() {
        val kid = SurvivorRelationClassifier.classify("102", "02", 15)
        val adult = SurvivorRelationClassifier.classify("102", "02", 16)
        assertEquals(SurvivorRelationKind.Kid, kid.kind)
        assertEquals(SurvivorRelationKind.AdultDaughter, adult.kind)
        assertEquals(SurvivorDependencyType.Daughter, adult.dependencyType)
        assertEquals(
            listOf(
                SurvivorDocumentType.IdFirstPage,
                SurvivorDocumentType.SpouseIdPage,
            ),
            adult.kind.requiredDocuments(),
        )
    }

    @Test
    fun parentTendency_mapsToParent() {
        val result = SurvivorRelationClassifier.classify("106", "01", 70)
        assertEquals(SurvivorRelationKind.Parent, result.kind)
        assertEquals(SurvivorDependencyType.Parent, result.dependencyType)
    }

    @Test
    fun genderAwareChildTendency_usesGender() {
        val son = SurvivorRelationClassifier.classify("111", "01", 20)
        val daughter = SurvivorRelationClassifier.classify("111", "02", 20)
        assertEquals(SurvivorRelationKind.AdultSon, son.kind)
        assertEquals(SurvivorRelationKind.AdultDaughter, daughter.kind)
    }

    @Test
    fun unknownTendency_hasNoDependencyAndUnknownDocs() {
        val result = SurvivorRelationClassifier.classify("999", "01", 30)
        assertEquals(SurvivorRelationKind.Unknown, result.kind)
        assertNull(result.dependencyType)
        assertEquals(
            listOf(
                SurvivorDocumentType.IdFirstPage,
                SurvivorDocumentType.SpouseIdPage,
            ),
            result.kind.requiredDocuments(),
        )
    }

    @Test
    fun ageFromBirthTimestamp_usesDayDiffOver365() {
        val birth = 0L
        val now = 365L * 24L * 60L * 60L * 1000L * 20L
        assertEquals(20, SurvivorRelationClassifier.ageYearsFromBirthTimestamp(birth, now))
    }

    @Test
    fun blankBirthDate_fallsBackToTwenty() {
        assertEquals(20, SurvivorRelationClassifier.ageYearsFromBirthDateString("", 1_000L))
    }
}
