package com.tamin.taminhamrah.feature.contracts.di

import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowQualifiers
import com.tamin.taminhamrah.feature.contracts.flow.config.FreelanceContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.HousewifeContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.OptionalContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.StudentContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.ui.ContractFlowViewModel
import com.tamin.taminhamrah.feature.contracts.ui.ContractsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private fun Module.contractFlowViewModel(qualifier: String, config: ContractFlowConfig) {
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
            makeOptionalContractByGuardianUseCase = get(),
            makeFreelanceContractByGuardianUseCase = get(),
            saveContactUseCase = get(),
            uploadImageUseCase = get(),
            subdominantUseCase = get(),
        )
    }
}

val contractsModule = module {
    viewModelOf(::ContractsViewModel)
    contractFlowViewModel(ContractFlowQualifiers.STUDENT, StudentContractFlowConfig())
    contractFlowViewModel(ContractFlowQualifiers.FREELANCE, FreelanceContractFlowConfig())
    contractFlowViewModel(ContractFlowQualifiers.HOUSEWIFE, HousewifeContractFlowConfig())
    contractFlowViewModel(ContractFlowQualifiers.OPTIONAL, OptionalContractFlowConfig())
}
