package com.tamin.taminhamrah.feature.deferredInstallment.di

import com.tamin.taminhamrah.feature.deferredInstallment.ui.DeferredInstallmentViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val deferredInstallmentModule = module {
    viewModelOf(::DeferredInstallmentViewModel)
}
