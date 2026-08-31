package com.tamin.taminhamrah.feature.freelanceContract

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contractFlow.config.SPECIAL_INSURED_STEPS
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
    override val steps = SPECIAL_INSURED_STEPS
    override val requiresFreeJob = true
    override val usesFreelancePremiumRange = true
    override val isOptionalInsurance = false
    override val hasUploadImageStep = true
    override val hasTreatmentSupportStep = true
    override val hasPremiumRateStep = true
    override val rulesPdfPath = "rules.pdf"
    override val requiresFemaleGender = false
    override val fixedFreeJobCode = null
    override val allowsOnlinePaymentAfterSubmit = true
}
