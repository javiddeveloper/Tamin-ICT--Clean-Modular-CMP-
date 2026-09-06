package com.tamin.taminhamrah.feature.contracts.di

import com.tamin.taminhamrah.feature.contracts.ui.ContractsViewModel
import com.tamin.taminhamrah.feature.contracts.ui.affairs.ContractAffairsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val contractsModule = module {
    viewModelOf(::ContractsViewModel)
    viewModelOf(::ContractAffairsViewModel)
}
