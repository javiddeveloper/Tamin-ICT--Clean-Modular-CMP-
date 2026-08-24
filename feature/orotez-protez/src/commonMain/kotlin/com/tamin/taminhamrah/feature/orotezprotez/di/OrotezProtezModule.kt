package com.tamin.taminhamrah.feature.orotezprotez.di

import com.tamin.taminhamrah.feature.orotezprotez.ui.OrotezProtezViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val orotezProtezModule = module {
    viewModelOf(::OrotezProtezViewModel)
}
