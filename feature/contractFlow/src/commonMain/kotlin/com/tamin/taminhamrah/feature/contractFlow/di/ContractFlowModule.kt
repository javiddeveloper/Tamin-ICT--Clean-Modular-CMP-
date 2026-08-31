package com.tamin.taminhamrah.feature.contractFlow.di

import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contractFlow.ui.ContractFlowViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named

fun Module.contractFlowViewModel(qualifier: String, config: ContractFlowConfig) {
    viewModel(named(qualifier)) {
        ContractFlowViewModel(
            config = config,
            getRegistrationInfoUseCase = get(),
            getContractsUseCase = get(),
            identityInfoUseCase = get(),
            getBranchesUseCase = get(),
            getSpcPremiumRatesUseCase = get(),
            getFreelancePremiumRangeUseCase = get(),
            getOptionalPremiumRangeUseCase = get(),
            calculateFreelanceSalaryUseCase = get(),
            calculateOptionalSalaryUseCase = get(),
            getFreeJobWagesUseCase = get(),
            checkRedCrossStatusUseCase = get(),
            checkMedicalStudentUseCase = get(),
            makeContractUseCase = get(),
            saveContactUseCase = get(),
            uploadImageUseCase = get(),
        )
    }
}
