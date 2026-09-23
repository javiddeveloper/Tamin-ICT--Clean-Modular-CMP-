package com.tamin.taminhamrah.feature.objectionInsurance.di

import com.tamin.taminhamrah.feature.objectionInsurance.ui.ObjectionInsuranceViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val objectionInsuranceModule = module {
    viewModelOf(::ObjectionInsuranceViewModel)
}
