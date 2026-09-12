package com.tamin.taminhamrah.feature.weddingPresent.di

import com.tamin.taminhamrah.feature.weddingPresent.ui.WeddingPresentViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val weddingPresentModule = module {
    viewModelOf(::WeddingPresentViewModel)
}
