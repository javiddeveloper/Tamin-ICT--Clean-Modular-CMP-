package com.tamin.taminhamrah.feature.optionalContract.di

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowQualifiers
import com.tamin.taminhamrah.feature.contractFlow.di.contractFlowViewModel
import com.tamin.taminhamrah.feature.optionalContract.OptionalContractFlowConfig
import org.koin.dsl.module

val optionalContractModule = module {
    contractFlowViewModel(ContractFlowQualifiers.OPTIONAL, OptionalContractFlowConfig())
}
