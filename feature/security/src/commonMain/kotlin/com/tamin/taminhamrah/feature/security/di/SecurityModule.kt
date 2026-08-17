package com.tamin.taminhamrah.feature.security.di

import com.tamin.taminhamrah.feature.security.ui.SecurityViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val securityModule = module {
    viewModelOf(::SecurityViewModel)
}
