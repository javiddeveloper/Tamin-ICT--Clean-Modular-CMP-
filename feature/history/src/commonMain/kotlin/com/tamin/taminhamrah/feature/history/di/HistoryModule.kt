package com.tamin.taminhamrah.feature.history.di

import com.tamin.taminhamrah.feature.history.ui.HistoryViewModel
import com.tamin.taminhamrah.feature.history.ui.jobinfo.HistoryJobInfoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val historyModule = module {
    viewModelOf(::HistoryViewModel)
    viewModelOf(::HistoryJobInfoViewModel)
}
