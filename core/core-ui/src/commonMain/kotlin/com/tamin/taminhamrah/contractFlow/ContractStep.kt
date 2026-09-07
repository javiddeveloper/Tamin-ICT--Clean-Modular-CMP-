package com.tamin.taminhamrah.contractFlow

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.*

enum class ContractStep(
    val titleRes: StringResource,
    val descRes: StringResource = Res.string.contract_flow_step_not_implemented,
) {
    STEP_REGISTRATION(
        titleRes = Res.string.contract_hero_step_1_title,
        descRes = Res.string.contract_hero_step_1_desc,
    ),
    STEP_AUTHORIZATION(
        titleRes = Res.string.contract_step_authorization,
        descRes = Res.string.contract_hero_step_1_desc,
    ),
    STEP_CONTRACT_TERMS(
        titleRes = Res.string.contract_hero_step_2_title,
        descRes = Res.string.contract_hero_step_2_desc,
    ),
    STEP_USER_INFO(
        titleRes = Res.string.contract_hero_step_3_title,
        descRes = Res.string.contract_hero_step_3_desc,
    ),
    STEP_CONTRACT_APPLICANT(
        titleRes = Res.string.contract_hero_step_4_title,
        descRes = Res.string.contract_hero_step_4_desc,
    ),
    STEP_SELECT_BRANCH(
        titleRes = Res.string.contract_hero_step_5_title,
        descRes = Res.string.contract_hero_step_5_desc,
    ),
    STEP_UPLOAD_IMAGE(
        titleRes = Res.string.contract_hero_step_6_title,
        descRes = Res.string.contract_hero_step_6_desc,
    ),
    STEP_JOB_TITLE(
        titleRes = Res.string.contract_hero_step_job_title,
        descRes = Res.string.contract_hero_step_job_title_desc,
    ),
    STEP_TREATMENT_SUPPORT(
        titleRes = Res.string.contract_step_treatment_support,
        descRes = Res.string.contract_hero_step_treatment_desc,
    ),
    STEP_INSURANCE_PREMIUM(
        titleRes = Res.string.contract_hero_step_7_title,
        descRes = Res.string.contract_hero_step_7_desc,
    ),
    STEP_SALARY(
        titleRes = Res.string.contract_hero_step_8_title,
        descRes = Res.string.contract_hero_step_8_desc,
    ),
    STEP_SUBMIT_CONTRACT(
        titleRes = Res.string.contract_hero_step_9_title,
        descRes = Res.string.contract_hero_step_9_desc,
    ),
    ;

    companion object {
        /** Special-insured flows: rate + wage are one combined step (design: step 8 of 9). */
        val STUDENT_STEPS: List<ContractStep> = listOf(
            STEP_REGISTRATION,
            STEP_CONTRACT_TERMS,
            STEP_USER_INFO,
            STEP_CONTRACT_APPLICANT,
            STEP_SELECT_BRANCH,
            STEP_UPLOAD_IMAGE,
            STEP_TREATMENT_SUPPORT,
            STEP_INSURANCE_PREMIUM,
            STEP_SUBMIT_CONTRACT,
        )

        /** Freelance: dedicated job-title step before treatment (design: step 7 of 10). */
        val FREELANCE_STEPS: List<ContractStep> = listOf(
            STEP_REGISTRATION,
            STEP_CONTRACT_TERMS,
            STEP_USER_INFO,
            STEP_CONTRACT_APPLICANT,
            STEP_SELECT_BRANCH,
            STEP_UPLOAD_IMAGE,
            STEP_JOB_TITLE,
            STEP_TREATMENT_SUPPORT,
            STEP_INSURANCE_PREMIUM,
            STEP_SUBMIT_CONTRACT,
        )

        val OPTIONAL_STEPS: List<ContractStep> = listOf(
            STEP_REGISTRATION,
            STEP_CONTRACT_TERMS,
            STEP_USER_INFO,
            STEP_CONTRACT_APPLICANT,
            STEP_SELECT_BRANCH,
            STEP_SALARY,
            STEP_SUBMIT_CONTRACT,
        )

        fun stepsFor(
            includeUploadImage: Boolean,
            includeTreatmentSupport: Boolean,
            includePremiumRate: Boolean,
            includeAuthorizationSeparate: Boolean = false,
            includeJobTitle: Boolean = false,
        ): List<ContractStep> = entries.filter { step ->
            when (step) {
                STEP_AUTHORIZATION -> includeAuthorizationSeparate
                STEP_UPLOAD_IMAGE -> includeUploadImage
                STEP_JOB_TITLE -> includeJobTitle
                STEP_TREATMENT_SUPPORT -> includeTreatmentSupport
                STEP_INSURANCE_PREMIUM -> includePremiumRate
                else -> true
            }
        }
    }
}

fun List<ContractStep>.nextStep(current: ContractStep): ContractStep? {
    val currentIndex = indexOf(current)
    if (currentIndex == -1) return firstOrNull()
    return getOrNull(currentIndex + 1)
}

fun List<ContractStep>.previousStep(current: ContractStep): ContractStep? {
    val currentIndex = indexOf(current)
    if (currentIndex <= 0) return null
    return getOrNull(currentIndex - 1)
}

fun List<ContractStep>.displayNumber(step: ContractStep): Int {
    val index = indexOf(step)
    return if (index == -1) 0 else index + 1
}

fun List<ContractStep>.isFirstStep(step: ContractStep): Boolean = firstOrNull() == step

fun List<ContractStep>.isLastStep(step: ContractStep): Boolean = lastOrNull() == step

fun ContractStep.isEditableFromSummary(): Boolean = when (this) {
    ContractStep.STEP_USER_INFO,
    ContractStep.STEP_CONTRACT_APPLICANT,
    ContractStep.STEP_SELECT_BRANCH,
    ContractStep.STEP_UPLOAD_IMAGE,
    ContractStep.STEP_JOB_TITLE,
    ContractStep.STEP_TREATMENT_SUPPORT,
    ContractStep.STEP_INSURANCE_PREMIUM,
    ContractStep.STEP_SALARY,
    -> true
    else -> false
}
