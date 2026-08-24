package com.tamin.taminhamrah.feature.pensionSurvivor.di

import com.tamin.taminhamrah.feature.pensionSurvivor.ui.PensionSurvivorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val pensionSurvivorModule = module {
    viewModelOf(::PensionSurvivorViewModel)
}
