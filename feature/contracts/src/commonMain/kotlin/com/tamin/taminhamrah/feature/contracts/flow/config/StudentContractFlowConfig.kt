package com.tamin.taminhamrah.feature.contracts.flow.config

import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.SPECIAL_INSURED_STEPS
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.contracts.ContractFreeJobCode
import com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode
import com.tamin.taminhamrah.ui.contractFlow.ContractRulesCopies
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_student_insurance_type
import taminx.core.core_ui.contract_student_screen_title

class StudentContractFlowConfig : ContractFlowConfig {
    override val featureFlag = FeatureFlag.STUDENT_INSURANCE
    override val screenTitleRes = Res.string.contract_student_screen_title
    override val insuranceTypeLabelRes = Res.string.contract_student_insurance_type
    override val agreementContractLabelRes = Res.string.contract_student_insurance_type
    override val premiumTypeCode = ContractPremiumTypeCode.FREELANCE
    override val steps = SPECIAL_INSURED_STEPS
    override val requiresFreeJob = false
    override val usesFreelancePremiumRange = true
    override val isOptionalInsurance = false
    override val hasUploadImageStep = true
    override val hasTreatmentSupportStep = true
    override val hasPremiumRateStep = true
    override val rulesPdfPath = "rules.pdf"
    override val rulesCopy = ContractRulesCopies.Student
    override val requiresFemaleGender = false
    override val fixedFreeJobCode = ContractFreeJobCode.STUDENT_CONTRACT_CODE
    override val allowsOnlinePaymentAfterSubmit = true
}
