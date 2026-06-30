package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.ui.MainViewModel
import com.tamin.taminhamrah.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
}
