package com.tamin.taminhamrah.feature.settings.di

import com.tamin.taminhamrah.feature.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
