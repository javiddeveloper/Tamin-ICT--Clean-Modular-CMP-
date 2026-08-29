package com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.pension_survivor_relation_child
import taminx.core.core_ui.pension_survivor_relation_daughter
import taminx.core.core_ui.pension_survivor_relation_father
import taminx.core.core_ui.pension_survivor_relation_mother
import taminx.core.core_ui.pension_survivor_relation_parents
import taminx.core.core_ui.pension_survivor_relation_son
import taminx.core.core_ui.pension_survivor_relation_spouse
import taminx.core.core_ui.pension_survivor_relation_step_child
import taminx.core.core_ui.pension_survivor_relation_survivor

/**
 * Ports legacy SurvivorInfoFragment.getRelationType / Utility.getTendencyResId / Utility.getAge.
 */
object SurvivorRelationClassifier {

    private val parentTendencyCodes = setOf("106", "110")
    private val sonTendencyCodes = setOf("101", "104")
    private val daughterTendencyCodes = setOf("102", "105")
    private val childByGenderTendencyCodes = setOf("111", "112", "117", "133", "118", "123")
    private val partnerTendencyCodes = setOf("100", "103", "107", "108", "109")
    private val stepChildTendencyCodes = setOf("133", "118", "123")
    private val childGenderAwareTitleCodes = setOf("111", "112", "117")

    private const val GENDER_MALE = "01"
    private const val GENDER_FEMALE = "02"
    private const val SON_ADULT_MIN_AGE = 19
    private const val DAUGHTER_ADULT_MIN_AGE = 16
    private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    private const val FALLBACK_AGE_YEARS = 20

    fun classify(
        tendencyCode: String,
        genderCode: String,
        ageYears: Int,
    ): SurvivorRelationClassification {
        return when (tendencyCode) {
            in parentTendencyCodes -> SurvivorRelationClassification(
                kind = SurvivorRelationKind.Parent,
                dependencyType = SurvivorDependencyType.Parent,
            )
            in sonTendencyCodes -> sonClassification(ageYears)
            in daughterTendencyCodes -> daughterClassification(ageYears)
            in childByGenderTendencyCodes -> when (genderCode) {
                GENDER_MALE -> sonClassification(ageYears)
                GENDER_FEMALE -> daughterClassification(ageYears)
                else -> SurvivorRelationClassification(
                    kind = SurvivorRelationKind.Kid,
                    dependencyType = null,
                )
            }
            in partnerTendencyCodes -> SurvivorRelationClassification(
                kind = SurvivorRelationKind.Partner,
                dependencyType = SurvivorDependencyType.Spouse,
            )
            else -> SurvivorRelationClassification(
                kind = SurvivorRelationKind.Unknown,
                dependencyType = null,
            )
        }
    }

    fun relationTitleRes(tendencyCode: String, genderCode: String): StringResource? {
        return when (tendencyCode) {
            "124" -> Res.string.pension_survivor_relation_survivor
            in parentTendencyCodes -> when (genderCode) {
                GENDER_MALE -> Res.string.pension_survivor_relation_father
                GENDER_FEMALE -> Res.string.pension_survivor_relation_mother
                else -> Res.string.pension_survivor_relation_parents
            }
            in sonTendencyCodes -> Res.string.pension_survivor_relation_son
            in daughterTendencyCodes -> Res.string.pension_survivor_relation_daughter
            in childGenderAwareTitleCodes -> when (genderCode) {
                GENDER_MALE -> Res.string.pension_survivor_relation_son
                GENDER_FEMALE -> Res.string.pension_survivor_relation_daughter
                else -> Res.string.pension_survivor_relation_child
            }
            in stepChildTendencyCodes -> Res.string.pension_survivor_relation_step_child
            in partnerTendencyCodes -> Res.string.pension_survivor_relation_spouse
            else -> null
        }
    }

    /**
     * Legacy Utility.getAge: day-diff / 365. Blank/invalid DOB string returns 20.
     */
    fun ageYearsFromBirthTimestamp(
        birthDateMs: Long?,
        nowMs: Long,
    ): Int {
        if (birthDateMs == null || birthDateMs <= 0L) return FALLBACK_AGE_YEARS
        val diffDays = ((nowMs - birthDateMs) / MILLIS_PER_DAY).coerceAtLeast(0L)
        return (diffDays / 365L).toInt()
    }

    fun ageYearsFromBirthDateString(
        dateOfBirth: String,
        nowMs: Long,
    ): Int {
        if (dateOfBirth.isBlank()) return FALLBACK_AGE_YEARS
        val birthDateMs = dateOfBirth.toLongOrNull() ?: return FALLBACK_AGE_YEARS
        return ageYearsFromBirthTimestamp(birthDateMs, nowMs)
    }

    private fun sonClassification(ageYears: Int): SurvivorRelationClassification {
        val kind = if (ageYears >= SON_ADULT_MIN_AGE) {
            SurvivorRelationKind.AdultSon
        } else {
            SurvivorRelationKind.Kid
        }
        return SurvivorRelationClassification(
            kind = kind,
            dependencyType = SurvivorDependencyType.Son,
        )
    }

    private fun daughterClassification(ageYears: Int): SurvivorRelationClassification {
        val kind = if (ageYears >= DAUGHTER_ADULT_MIN_AGE) {
            SurvivorRelationKind.AdultDaughter
        } else {
            SurvivorRelationKind.Kid
        }
        return SurvivorRelationClassification(
            kind = kind,
            dependencyType = SurvivorDependencyType.Daughter,
        )
    }
}
