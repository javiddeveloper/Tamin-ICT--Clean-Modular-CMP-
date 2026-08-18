package com.tamin.taminhamrah.feature.addDependent.di

import com.tamin.taminhamrah.feature.addDependent.ui.AddDependentViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addDependentModule = module {
    viewModelOf(::AddDependentViewModel)
}
