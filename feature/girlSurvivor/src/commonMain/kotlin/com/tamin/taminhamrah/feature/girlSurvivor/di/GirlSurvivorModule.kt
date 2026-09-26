package com.tamin.taminhamrah.feature.girlSurvivor.di

import com.tamin.taminhamrah.feature.girlSurvivor.ui.GirlSurvivorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val girlSurvivorModule = module {
    // viewModelOf(::GirlSurvivorViewModel) also resolves the default resolveString
    // parameter (a suspend Function2). Nothing registers that type, so creation crashes.
    viewModel {
        GirlSurvivorViewModel(
            getPersonalInfoUseCase = get(),
            checkGirlSurvivorConditionsUseCase = get(),
            getGirlSurvivorReportUseCase = get(),
            confirmGirlSurvivorUseCase = get(),
        )
    }
}
