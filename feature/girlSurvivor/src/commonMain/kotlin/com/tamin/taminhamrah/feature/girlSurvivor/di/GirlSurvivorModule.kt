package com.tamin.taminhamrah.feature.girlSurvivor.di

import com.tamin.taminhamrah.feature.girlSurvivor.ui.GirlSurvivorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val girlSurvivorModule = module {
    viewModelOf(::GirlSurvivorViewModel)
}
