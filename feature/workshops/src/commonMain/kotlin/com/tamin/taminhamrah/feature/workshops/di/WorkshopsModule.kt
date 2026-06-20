package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    viewModelOf(::WorkshopsViewModel)
}
