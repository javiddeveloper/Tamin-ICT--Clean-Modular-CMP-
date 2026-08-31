package com.tamin.taminhamrah.contractFlow

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_step_authorization
import taminx.core.core_ui.contract_step_contract_applicant
import taminx.core.core_ui.contract_step_contract_terms
import taminx.core.core_ui.contract_step_insurance_premium
import taminx.core.core_ui.contract_step_registration
import taminx.core.core_ui.contract_step_salary
import taminx.core.core_ui.contract_step_select_branch
import taminx.core.core_ui.contract_step_submit_contract
import taminx.core.core_ui.contract_step_treatment_support
import taminx.core.core_ui.contract_step_upload_image
import taminx.core.core_ui.contract_step_user_info

enum class ContractStep(val titleRes: StringResource) {
    STEP_REGISTRATION(Res.string.contract_step_registration),
    STEP_AUTHORIZATION(Res.string.contract_step_authorization),
    STEP_CONTRACT_TERMS(Res.string.contract_step_contract_terms),
    STEP_USER_INFO(Res.string.contract_step_user_info),
    STEP_CONTRACT_APPLICANT(Res.string.contract_step_contract_applicant),
    STEP_SELECT_BRANCH(Res.string.contract_step_select_branch),
    STEP_UPLOAD_IMAGE(Res.string.contract_step_upload_image),
    STEP_TREATMENT_SUPPORT(Res.string.contract_step_treatment_support),
    STEP_INSURANCE_PREMIUM(Res.string.contract_step_insurance_premium),
    STEP_SALARY(Res.string.contract_step_salary),
    STEP_SUBMIT_CONTRACT(Res.string.contract_step_submit_contract),
    ;

    companion object {
        val OPTIONAL_STEPS: List<ContractStep> = listOf(
            STEP_REGISTRATION,
            STEP_AUTHORIZATION,
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
        ): List<ContractStep> = entries.filter { step ->
            when (step) {
                STEP_UPLOAD_IMAGE -> includeUploadImage
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
