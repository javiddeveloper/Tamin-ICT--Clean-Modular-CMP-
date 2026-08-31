package com.tamin.taminhamrah.feature.pregnancyPay.di

import com.tamin.taminhamrah.feature.pregnancyPay.ui.PregnancyPayViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val pregnancyPayModule = module {
    viewModelOf(::PregnancyPayViewModel)
}
