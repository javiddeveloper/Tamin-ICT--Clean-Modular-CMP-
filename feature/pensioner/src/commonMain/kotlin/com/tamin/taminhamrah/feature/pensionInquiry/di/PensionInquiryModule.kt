package com.tamin.taminhamrah.feature.pensionInquiry.di

import com.tamin.taminhamrah.feature.pensionInquiry.ui.calculatePension.CalculatePensionViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.prescription.PrescriptionViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.deservedTreatment.DeservedTreatmentViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.PayRollViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.EdictViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.IssuanceCertificateViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.pensionSurvivor.PensionSurvivorViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.DisabilityPensionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val pensionInquiryModule = module {
    viewModelOf(::CalculatePensionViewModel)
    viewModelOf(::PrescriptionViewModel)
    viewModelOf(::DeservedTreatmentViewModel)
    viewModelOf(::PayRollViewModel)
    viewModelOf(::EdictViewModel)
    viewModelOf(::IssuanceCertificateViewModel)
    viewModelOf(::PensionSurvivorViewModel)
    viewModelOf(::DisabilityPensionViewModel)
}

