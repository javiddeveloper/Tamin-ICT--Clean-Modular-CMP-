package com.tamin.taminhamrah.feature.developerOptions.di

import com.tamin.taminhamrah.feature.developerOptions.ui.DeveloperOptionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val developerOptionsModule = module {
    viewModelOf(::DeveloperOptionsViewModel)
}
