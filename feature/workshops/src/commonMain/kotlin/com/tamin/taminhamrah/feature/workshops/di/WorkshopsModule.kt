package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.demandDocuments.DemandDocumentsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    // Shared by the three forms that attach evidence.
    factoryOf(::WorkshopAttachmentUploader)

    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::ObjectionableDebitViewModel)
    viewModelOf(::WorkshopDebtInquiryViewModel)
    viewModelOf(::PaymentSheetsViewModel)
    viewModelOf(::WorkshopDebitViewModel)
    viewModelOf(::DemandDocumentsViewModel)
}
