package com.tamin.taminhamrah.feature.retirementPension.di

import com.tamin.taminhamrah.feature.retirementPension.ui.RetirementPensionViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val retirementPensionModule: Module = module {
    viewModelOf(::RetirementPensionViewModel)
}
