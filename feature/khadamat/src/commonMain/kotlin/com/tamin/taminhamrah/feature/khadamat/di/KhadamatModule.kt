package com.tamin.taminhamrah.feature.khadamat.di

import com.tamin.taminhamrah.feature.khadamat.ui.KhadamatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val khadamatModule = module {
    viewModelOf(::KhadamatViewModel)
}
