package com.tamin.taminhamrah.feature.fractionContract.di

import com.tamin.taminhamrah.feature.fractionContract.ui.FractionContractViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val fractionContractModule = module {
    viewModelOf(::FractionContractViewModel)
}
