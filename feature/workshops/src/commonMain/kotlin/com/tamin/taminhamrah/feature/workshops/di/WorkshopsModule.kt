package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.ContractRowsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add.AddLegalRepresentativeViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list.LegalRepresentativeListViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp.LegalRepresentativeOtpViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops.LegalRepresentativeWorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document.ObjectionDocumentViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list.ObjectionStatusViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms.ObjectionSmsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    // Shared by the three forms that attach evidence.
    factoryOf(::WorkshopAttachmentUploader)

    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::WorkshopDebtInquiryViewModel)
    viewModelOf(::ObjectionableDebitViewModel)
    viewModelOf(::PaymentSheetsViewModel)
    viewModelOf(::LegalRepresentativeWorkshopsViewModel)
    viewModelOf(::LegalRepresentativeOtpViewModel)
    viewModelOf(::LegalRepresentativeListViewModel)
    viewModelOf(::AddLegalRepresentativeViewModel)
    viewModelOf(::ContractRowsViewModel)
    viewModelOf(::ObjectionStatusViewModel)
    viewModelOf(::ObjectionSmsViewModel)
    viewModelOf(::ObjectionDocumentViewModel)
}
