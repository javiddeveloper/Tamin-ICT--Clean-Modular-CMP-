package com.tamin.taminhamrah.feature.taminServices.di

import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val TaminServicesModule = module {
    viewModelOf(::TamminServicesViewModel)
}
