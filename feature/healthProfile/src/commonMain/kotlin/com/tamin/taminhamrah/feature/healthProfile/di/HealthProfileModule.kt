package com.tamin.taminhamrah.feature.healthProfile.di

import com.tamin.taminhamrah.feature.healthProfile.ui.HealthProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val healthProfileModule = module {
    viewModelOf(::HealthProfileViewModel)
}
