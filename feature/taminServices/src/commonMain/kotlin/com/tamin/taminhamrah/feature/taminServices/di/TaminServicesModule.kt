package com.tamin.taminhamrah.feature.taminServices.di

import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.SendHistoryToInstitutionsViewModel
import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.history.SendToInstitutionUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val TaminServicesModule = module {
    viewModelOf(::TamminServicesViewModel)
    factoryOf(::GetUserInfosUseCase)
    factoryOf(::SendToInstitutionUseCase)
    factoryOf(::GetPensionerIdUseCase)
    viewModelOf(::SendHistoryToInstitutionsViewModel)
}

