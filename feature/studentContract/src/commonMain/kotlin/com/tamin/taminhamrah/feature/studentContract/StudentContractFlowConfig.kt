package com.tamin.taminhamrah.feature.studentContract

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contractFlow.config.SPECIAL_INSURED_STEPS
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.contracts.ContractFreeJobCode

class StudentContractFlowConfig : ContractFlowConfig {
    override val featureFlag = FeatureFlag.STUDENT_INSURANCE
    override val screenTitle = "انعقاد قرارداد بیمه دانشجویی"
    override val insuranceTypeLabel = "بیمه دانشجویی"
    override val agreementContractLabel = "بیمه دانشجویی"
    override val premiumTypeCode = "01"
    override val steps = SPECIAL_INSURED_STEPS
    override val requiresFreeJob = false
    override val usesFreelancePremiumRange = true
    override val isOptionalInsurance = false
    override val hasUploadImageStep = true
    override val hasTreatmentSupportStep = true
    override val hasPremiumRateStep = true
    override val rulesPdfPath = "rules.pdf"
    override val requiresFemaleGender = false
    override val fixedFreeJobCode = ContractFreeJobCode.STUDENT_CONTRACT_CODE
    override val allowsOnlinePaymentAfterSubmit = true
}
