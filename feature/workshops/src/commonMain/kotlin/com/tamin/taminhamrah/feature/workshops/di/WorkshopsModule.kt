package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.AssignerContractsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.ContractRowsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.demandDocuments.DemandDocumentsViewModel
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
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryViewModel
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    // Shared by the three forms that attach evidence. The use case is Flow-shaped; the single
    // guid a form actually wants is taken here, so the forms never see the Flow.
    factory {
        val uploadImage: UploadImageUseCase = get()
        WorkshopAttachmentUploader { uploadImage(it).first() }
    }

    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::WorkshopDebtInquiryViewModel)
    viewModelOf(::ObjectionableDebitViewModel)
    viewModelOf(::PaymentSheetsViewModel)
    viewModelOf(::WorkshopDebitViewModel)
    viewModelOf(::DemandDocumentsViewModel)
    viewModelOf(::LegalRepresentativeWorkshopsViewModel)
    viewModelOf(::LegalRepresentativeOtpViewModel)
    viewModelOf(::LegalRepresentativeListViewModel)
    viewModelOf(::AddLegalRepresentativeViewModel)
    viewModelOf(::ContractRowsViewModel)
    viewModelOf(::AssignerContractsViewModel)
    viewModelOf(::SettlementRequestViewModel)
    viewModelOf(::ObjectionStatusViewModel)
    viewModelOf(::ObjectionSmsViewModel)
    viewModelOf(::ObjectionDocumentViewModel)
}
