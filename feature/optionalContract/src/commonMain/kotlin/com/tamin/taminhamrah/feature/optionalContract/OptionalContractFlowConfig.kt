package com.tamin.taminhamrah.feature.optionalContract

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contractFlow.config.OPTIONAL_FLOW_STEPS
import com.tamin.taminhamrah.model.common.FeatureFlag

class OptionalContractFlowConfig : ContractFlowConfig {
    override val featureFlag = FeatureFlag.OPTIONAL_INSURANCE
    override val screenTitle = "انعقاد قرارداد بیمه اختیاری"
    override val insuranceTypeLabel = "بیمه اختیاری"
    override val agreementContractLabel = "بیمه اختیاری"
    override val premiumTypeCode = "02"
    override val steps = OPTIONAL_FLOW_STEPS
    override val requiresFreeJob = false
    override val usesFreelancePremiumRange = false
    override val isOptionalInsurance = true
    override val hasUploadImageStep = false
    override val hasTreatmentSupportStep = false
    override val hasPremiumRateStep = false
    override val rulesPdfPath = "rules2.pdf"
    override val requiresFemaleGender = false
    override val fixedFreeJobCode = null
    override val allowsOnlinePaymentAfterSubmit = true
}
