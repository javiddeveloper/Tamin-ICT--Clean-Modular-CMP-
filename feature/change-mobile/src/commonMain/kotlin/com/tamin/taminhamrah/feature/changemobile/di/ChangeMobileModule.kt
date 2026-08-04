package com.tamin.taminhamrah.feature.changemobile.di

import com.tamin.taminhamrah.feature.changemobile.ui.ChangeMobileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val changeMobileModule = module {
    viewModelOf(::ChangeMobileViewModel)
}
