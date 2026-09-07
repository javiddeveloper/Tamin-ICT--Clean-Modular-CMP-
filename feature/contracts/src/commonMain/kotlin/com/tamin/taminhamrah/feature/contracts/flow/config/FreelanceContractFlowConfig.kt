package com.tamin.taminhamrah.feature.contracts.flow.config

import com.tamin.taminhamrah.contractFlow.ContractStep
import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowConfig
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_freelance_insurance_type
import taminx.core.core_ui.contract_freelance_screen_title

class FreelanceContractFlowConfig : ContractFlowConfig {
    override val featureFlag = FeatureFlag.FREELANCE_INSURANCE
    override val screenTitleRes = Res.string.contract_freelance_screen_title
    override val insuranceTypeLabelRes = Res.string.contract_freelance_insurance_type
    override val agreementContractLabelRes = Res.string.contract_freelance_insurance_type
    override val premiumTypeCode = ContractPremiumTypeCode.FREELANCE
    override val steps = ContractStep.FREELANCE_STEPS
    override val requiresFreeJob = true
    override val usesFreelancePremiumRange = true
    override val isOptionalInsurance = false
    override val hasUploadImageStep = true
    override val hasTreatmentSupportStep = true
    override val hasPremiumRateStep = true
    override val rulesPdfPath = ContractRulesPdf.SPECIAL_INSURED
    override val requiresFemaleGender = false
    override val fixedFreeJobCode = null
    override val allowsOnlinePaymentAfterSubmit = true
}
