package com.tamin.taminhamrah.feature.profile.di

import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
}
