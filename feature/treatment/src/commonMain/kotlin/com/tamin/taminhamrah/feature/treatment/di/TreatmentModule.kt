package com.tamin.taminhamrah.feature.treatment.di

import com.tamin.taminhamrah.feature.treatment.ui.TreatmentViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val treatmentModule = module {
    viewModelOf(::TreatmentViewModel)
}
