package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::PaymentSheetsViewModel)
    viewModelOf(::WorkshopDebitViewModel)
    viewModelOf(::WorkshopDebtInquiryViewModel)
}
