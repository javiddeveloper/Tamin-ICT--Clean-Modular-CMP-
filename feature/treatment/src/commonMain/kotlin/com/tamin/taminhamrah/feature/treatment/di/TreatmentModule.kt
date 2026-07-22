package com.tamin.taminhamrah.feature.treatment.di

import com.tamin.taminhamrah.feature.treatment.ui.TreatmentViewModel
import com.tamin.taminhamrah.feature.treatment.ui.healthProfile.HealthProfileViewModel
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val treatmentModule = module {
    viewModelOf(::TreatmentViewModel)
    viewModelOf(::HealthProfileViewModel)
    viewModelOf(::PrescriptionsViewModel)
}
