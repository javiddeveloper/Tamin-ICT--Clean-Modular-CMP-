package com.tamin.taminhamrah.feature.historyobjection.di

import com.tamin.taminhamrah.feature.historyobjection.ui.HistoryObjectionViewModel
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.HistoryObjectionStepperViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val historyObjectionModule = module {
    viewModelOf(::HistoryObjectionViewModel)
    viewModelOf(::HistoryObjectionStepperViewModel)
}
