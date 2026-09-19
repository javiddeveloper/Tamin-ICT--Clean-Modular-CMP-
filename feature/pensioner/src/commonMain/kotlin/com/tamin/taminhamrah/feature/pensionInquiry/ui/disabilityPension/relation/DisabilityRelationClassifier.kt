package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.relation

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
 * Ports legacy `Utility.getTendencyResId` — the server's `personal.relation` field on the
 * disability-dependent payload is not actually populated; the relation label must be derived
 * from `tendencyCode` (+ `genderCode` for the codes that are gender-ambiguous on their own).
 * Shares the same shared `pension_survivor_relation_*` strings as
 * `feature/pensionSurvivor`'s `SurvivorRelationClassifier` (same legacy source function) rather
 * than duplicating them, since this classifier can't import that feature-local object across
 * module boundaries.
 */
object DisabilityRelationClassifier {

    private val parentTendencyCodes = setOf("106", "110")
    private val sonTendencyCodes = setOf("101", "104")
    private val daughterTendencyCodes = setOf("102", "105")
    private val childGenderAwareTitleCodes = setOf("111", "112", "117")
    private val stepChildTendencyCodes = setOf("133", "118", "123")
    private val partnerTendencyCodes = setOf("100", "103", "107", "108", "109")

    private const val GENDER_MALE = "01"
    private const val GENDER_FEMALE = "02"

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
}
