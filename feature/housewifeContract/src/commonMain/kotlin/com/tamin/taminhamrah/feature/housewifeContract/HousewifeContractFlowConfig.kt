package com.tamin.taminhamrah.feature.housewifeContract

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contractFlow.config.SPECIAL_INSURED_STEPS
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.contracts.ContractFreeJobCode

class HousewifeContractFlowConfig : ContractFlowConfig {
    override val featureFlag = FeatureFlag.HOUSEWIFE_INSURANCE
    override val screenTitle = "انعقاد قرارداد بیمه زنان خانه‌دار"
    override val insuranceTypeLabel = "بیمه زنان خانه‌دار"
    override val agreementContractLabel = "بیمه زنان خانه‌دار"
    override val premiumTypeCode = "01"
    override val steps = SPECIAL_INSURED_STEPS
    override val requiresFreeJob = false
    override val usesFreelancePremiumRange = true
    override val isOptionalInsurance = false
    override val hasUploadImageStep = true
    override val hasTreatmentSupportStep = true
    override val hasPremiumRateStep = true
    override val rulesPdfPath = "rules.pdf"
    override val requiresFemaleGender = true
    override val fixedFreeJobCode = ContractFreeJobCode.WOMEN_CONTRACT_CODE
    override val allowsOnlinePaymentAfterSubmit = true
}
