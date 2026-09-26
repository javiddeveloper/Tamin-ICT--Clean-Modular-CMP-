package com.tamin.taminhamrah.feature.girlSurvivor.di

import com.tamin.taminhamrah.feature.girlSurvivor.ui.GirlSurvivorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val girlSurvivorModule = module {
    // Not viewModelOf(::GirlSurvivorViewModel): that asks Koin for every constructor parameter,
    // defaults included, and there is no definition for `resolveString` — the screen crashed on open.
    viewModel {
        GirlSurvivorViewModel(
            getPersonalInfoUseCase = get(),
            checkGirlSurvivorConditionsUseCase = get(),
            getGirlSurvivorReportUseCase = get(),
            confirmGirlSurvivorUseCase = get(),
        )
    }
}
