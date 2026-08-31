package com.tamin.taminhamrah.feature.housewifeContract.di

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowQualifiers
import com.tamin.taminhamrah.feature.contractFlow.di.contractFlowViewModel
import com.tamin.taminhamrah.feature.housewifeContract.HousewifeContractFlowConfig
import org.koin.dsl.module

val housewifeContractModule = module {
    contractFlowViewModel(ContractFlowQualifiers.HOUSEWIFE, HousewifeContractFlowConfig())
}
