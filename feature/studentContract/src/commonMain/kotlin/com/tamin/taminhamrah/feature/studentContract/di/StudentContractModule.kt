package com.tamin.taminhamrah.feature.studentContract.di

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowQualifiers
import com.tamin.taminhamrah.feature.contractFlow.di.contractFlowViewModel
import com.tamin.taminhamrah.feature.studentContract.StudentContractFlowConfig
import org.koin.dsl.module

val studentContractModule = module {
    contractFlowViewModel(ContractFlowQualifiers.STUDENT, StudentContractFlowConfig())
}
