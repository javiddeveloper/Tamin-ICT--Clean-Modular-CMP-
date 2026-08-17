package com.tamin.taminhamrah.feature.treatment.di

import com.tamin.taminhamrah.feature.treatment.ui.TreatmentViewModel
import com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts.TreatmentCostsViewModel
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations.MedicalConfirmationsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val treatmentModule = module {
    viewModelOf(::TreatmentViewModel)
    viewModelOf(::TreatmentCostsViewModel)
    viewModelOf(::PrescriptionsViewModel)
    viewModelOf(::MedicalConfirmationsViewModel)
}

