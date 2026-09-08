package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.AssignerContractsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.ContractRowsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add.AddLegalRepresentativeViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list.LegalRepresentativeListViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp.LegalRepresentativeOtpViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops.LegalRepresentativeWorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryViewModel
import com.tamin.taminhamrah.repository.WorkShopsRepository
import com.tamin.taminhamrah.useCases.workshops.GetAssignerContractsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetComputationalBasePdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetComputationalBasesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    // Shared by the three forms that attach evidence.
    factoryOf(::WorkshopAttachmentUploader)

    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::WorkshopDebtInquiryViewModel)
    viewModelOf(::PaymentSheetsViewModel)
    viewModelOf(::LegalRepresentativeWorkshopsViewModel)
    viewModelOf(::LegalRepresentativeOtpViewModel)
    viewModelOf(::LegalRepresentativeListViewModel)
    viewModelOf(::AddLegalRepresentativeViewModel)
    viewModelOf(::ContractRowsViewModel)
    viewModelOf(::AssignerContractsViewModel)

    // TEMPORARY - delete with AssignerContractsMock.kt before handover.
    if (USE_ASSIGNER_CONTRACTS_MOCK) {
        val mock: (org.koin.core.scope.Scope) -> WorkShopsRepository = {
            MockAssignerContractsRepository(it.get())
        }
        factory { GetAssignerContractsUseCase(mock(this)) }
        factory { GetComputationalBasesUseCase(mock(this)) }
        factory { GetComputationalBasePdfUseCase(mock(this)) }
    }
}
