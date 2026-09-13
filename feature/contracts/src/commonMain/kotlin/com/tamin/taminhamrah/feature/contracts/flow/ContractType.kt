package com.tamin.taminhamrah.feature.contracts.flow

import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowQualifiers
import com.tamin.taminhamrah.feature.contracts.flow.config.FreelanceContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.HousewifeContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.OptionalContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.StudentContractFlowConfig
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlinx.serialization.Serializable

@Serializable
enum class ContractType {
    STUDENT,
    FREELANCE,
    HOUSEWIFE,
    OPTIONAL,
    ;

    val koinQualifier: String
        get() = when (this) {
            STUDENT -> ContractFlowQualifiers.STUDENT
            FREELANCE -> ContractFlowQualifiers.FREELANCE
            HOUSEWIFE -> ContractFlowQualifiers.HOUSEWIFE
            OPTIONAL -> ContractFlowQualifiers.OPTIONAL
        }

    val featureFlag: FeatureFlag
        get() = when (this) {
            STUDENT -> FeatureFlag.STUDENT_INSURANCE
            FREELANCE -> FeatureFlag.FREELANCE_INSURANCE
            HOUSEWIFE -> FeatureFlag.HOUSEWIFE_INSURANCE
            OPTIONAL -> FeatureFlag.OPTIONAL_INSURANCE
        }

    fun createConfig(): ContractFlowConfig = when (this) {
        STUDENT -> StudentContractFlowConfig()
        FREELANCE -> FreelanceContractFlowConfig()
        HOUSEWIFE -> HousewifeContractFlowConfig()
        OPTIONAL -> OptionalContractFlowConfig()
    }

    companion object {
        fun fromFeatureFlag(flag: FeatureFlag): ContractType? = when (flag) {
            FeatureFlag.STUDENT_INSURANCE -> STUDENT
            FeatureFlag.FREELANCE_INSURANCE -> FREELANCE
            FeatureFlag.HOUSEWIFE_INSURANCE -> HOUSEWIFE
            FeatureFlag.OPTIONAL_INSURANCE -> OPTIONAL
            else -> null
        }
    }
}
