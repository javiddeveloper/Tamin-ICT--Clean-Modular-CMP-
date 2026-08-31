package com.tamin.taminhamrah.feature.freelanceContract

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contractFlow.config.SPECIAL_INSURED_STEPS
import com.tamin.taminhamrah.model.common.FeatureFlag

class FreelanceContractFlowConfig : ContractFlowConfig {
    override val featureFlag = FeatureFlag.FREELANCE_INSURANCE
    override val screenTitle = "انعقاد قرارداد بیمه صاحبان حرف و مشاغل آزاد"
    override val insuranceTypeLabel = "بیمه صاحبان حرف و مشاغل آزاد"
    override val agreementContractLabel = "بیمه صاحبان حرف و مشاغل آزاد"
    override val premiumTypeCode = "01"
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
