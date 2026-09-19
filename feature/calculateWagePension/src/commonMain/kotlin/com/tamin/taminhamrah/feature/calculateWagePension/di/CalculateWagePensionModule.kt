package com.tamin.taminhamrah.feature.calculateWagePension.di

import com.tamin.taminhamrah.feature.calculateWagePension.ui.CalculateWagePensionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val calculateWagePensionModule = module {
    viewModelOf(::CalculateWagePensionViewModel)
}
