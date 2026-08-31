package com.tamin.taminhamrah.feature.freelanceContract.di

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowQualifiers
import com.tamin.taminhamrah.feature.contractFlow.di.contractFlowViewModel
import com.tamin.taminhamrah.feature.freelanceContract.FreelanceContractFlowConfig
import org.koin.dsl.module

val freelanceContractModule = module {
    contractFlowViewModel(ContractFlowQualifiers.FREELANCE, FreelanceContractFlowConfig())
}
