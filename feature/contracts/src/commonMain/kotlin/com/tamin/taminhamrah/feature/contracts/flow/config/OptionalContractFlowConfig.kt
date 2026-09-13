package com.tamin.taminhamrah.feature.contracts.flow.config

import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.OPTIONAL_FLOW_STEPS
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode
import com.tamin.taminhamrah.ui.contractFlow.ContractRulesCopies
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_optional_insurance_type
import taminx.core.core_ui.contract_optional_screen_title

class OptionalContractFlowConfig : ContractFlowConfig {
    override val featureFlag = FeatureFlag.OPTIONAL_INSURANCE
    override val screenTitleRes = Res.string.contract_optional_screen_title
    override val insuranceTypeLabelRes = Res.string.contract_optional_insurance_type
    override val agreementContractLabelRes = Res.string.contract_optional_insurance_type
    override val premiumTypeCode = ContractPremiumTypeCode.OPTIONAL
    override val steps = OPTIONAL_FLOW_STEPS
    override val requiresFreeJob = false
    override val usesFreelancePremiumRange = false
    override val isOptionalInsurance = true
    override val hasUploadImageStep = false
    override val hasTreatmentSupportStep = false
    override val hasPremiumRateStep = false
    override val rulesPdfPath = "rules2.pdf"
    override val rulesCopy = ContractRulesCopies.Optional
    override val requiresFemaleGender = false
    override val fixedFreeJobCode = null
    override val allowsOnlinePaymentAfterSubmit = true
}
