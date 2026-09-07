package com.tamin.taminhamrah.feature.contracts.flow.config

import com.tamin.taminhamrah.contractFlow.ContractStep
import com.tamin.taminhamrah.contractFlow.displayNumber
import com.tamin.taminhamrah.contractFlow.isFirstStep
import com.tamin.taminhamrah.contractFlow.isLastStep
import com.tamin.taminhamrah.contractFlow.nextStep
import com.tamin.taminhamrah.contractFlow.previousStep
import com.tamin.taminhamrah.model.common.FeatureFlag
import org.jetbrains.compose.resources.StringResource

interface ContractFlowConfig {
    val featureFlag: FeatureFlag
    val screenTitleRes: StringResource
    val insuranceTypeLabelRes: StringResource
    val agreementContractLabelRes: StringResource
    val premiumTypeCode: String
    val steps: List<ContractStep>
    val requiresFreeJob: Boolean
    val usesFreelancePremiumRange: Boolean
    val isOptionalInsurance: Boolean
    val hasUploadImageStep: Boolean
    val hasTreatmentSupportStep: Boolean
    val hasPremiumRateStep: Boolean
    /**
     * Compose-resource file name under `files/` for ضوابط و مقررات.
     * Legacy: [ContractRulesPdf] — same assets as
     * `rulesAndRegulationsHtmlFile/{name}` in my-tamin-droid.
     */
    val rulesPdfPath: String
    val requiresFemaleGender: Boolean
    val fixedFreeJobCode: String?
    val allowsOnlinePaymentAfterSubmit: Boolean

    fun nextStep(current: ContractStep): ContractStep? = steps.nextStep(current)

    fun previousStep(current: ContractStep): ContractStep? = steps.previousStep(current)

    fun displayNumber(step: ContractStep): Int = steps.displayNumber(step)

    fun isFirstStep(step: ContractStep): Boolean = steps.isFirstStep(step)

    fun isLastStep(step: ContractStep): Boolean = steps.isLastStep(step)
}

/**
 * Legacy PDF assets for contract rules (InsuranceContractFragment / OptionalContractFragment).
 *
 * | Contract | Legacy fragment | Asset |
 * |---|---|---|
 * | Student / Freelance / Housewife | InsuranceContractFragment | rules.pdf |
 * | Optional | OptionalContractFragment | rules2.pdf |
 */
object ContractRulesPdf {
    const val SPECIAL_INSURED = "rules.pdf"
    const val OPTIONAL = "rules2.pdf"
}

val SPECIAL_INSURED_STEPS = ContractStep.STUDENT_STEPS

val OPTIONAL_FLOW_STEPS = ContractStep.OPTIONAL_STEPS
