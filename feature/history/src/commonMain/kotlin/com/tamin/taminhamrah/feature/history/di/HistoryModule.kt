package com.tamin.taminhamrah.feature.history.di

import com.tamin.taminhamrah.feature.history.ui.HistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val historyModule = module {
    viewModelOf(::HistoryViewModel)
}
