package com.tamin.taminhamrah.feature.treatment.di

import com.tamin.taminhamrah.feature.treatment.ui.TreatmentViewModel
import com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts.TreatmentCostsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val treatmentModule = module {
    viewModelOf(::TreatmentViewModel)
    viewModelOf(::TreatmentCostsViewModel)
}
