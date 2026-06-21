package com.tamin.taminhamrah.feature.contracts.di

import com.tamin.taminhamrah.feature.contracts.ui.ContractsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val contractsModule = module {
    viewModelOf(::ContractsViewModel)
}
