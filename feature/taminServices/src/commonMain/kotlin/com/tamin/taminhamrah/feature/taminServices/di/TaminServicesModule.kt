package com.tamin.taminhamrah.feature.taminServices.di

import com.tamin.taminhamrah.feature.taminServices.occurrence.OccurrenceViewModel
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.SendHistoryToInstitutionsViewModel
import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
import com.tamin.taminhamrah.feature.taminServices.workersPayment.WorkersPaymentViewModel
import com.tamin.taminhamrah.useCases.workersPayment.GetWorkersPaymentInfoUseCase
import com.tamin.taminhamrah.useCases.workersPayment.InspectWorkersPaymentTicketUseCase
import com.tamin.taminhamrah.useCases.workersPayment.PayWorkersDebitUseCase
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.history.SendToInstitutionUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetAllWorkshopsUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetInsuredRelationUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetOccurrenceDocTypesUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetOccurrencePersonalInfoUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetWorkshopSpecUseCase
import com.tamin.taminhamrah.useCases.occurrence.SubmitOccurrenceUseCase
import com.tamin.taminhamrah.useCases.occurrence.UploadOccurrenceImageUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInsurancePageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetBranchPageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInspectionReportPDFUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobPageUseCase
import com.tamin.taminhamrah.useCases.inspection.SubmitInspectionUseCase
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionViewModel
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesViewModel
import com.tamin.taminhamrah.feature.taminServices.verifier.ConstructionWorkersPaymentVerifier
import com.tamin.taminhamrah.repository.payment.PaymentVerifier
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val TaminServicesModule = module {
    single<PaymentVerifier>(named("constructionWorkersPaymentVerifier")) {
        ConstructionWorkersPaymentVerifier(get())
    }
    viewModelOf(::TamminServicesViewModel)
    factoryOf(::GetUserInfosUseCase)
    factoryOf(::SendToInstitutionUseCase)
    factoryOf(::GetPensionerIdUseCase)
    viewModelOf(::SendHistoryToInstitutionsViewModel)

    viewModelOf(::OccurrenceViewModel)
    factoryOf(::GetOccurrencePersonalInfoUseCase)
    factoryOf(::GetAllWorkshopsUseCase)
    factoryOf(::GetWorkshopSpecUseCase)
    factoryOf(::GetInsuredRelationUseCase)
    factoryOf(::GetOccurrenceDocTypesUseCase)
    factoryOf(::UploadOccurrenceImageUseCase)
    factoryOf(::SubmitOccurrenceUseCase)
    factoryOf(::GetInsurancePageUseCase)
    factoryOf(::GetBranchPageUseCase)
    factoryOf(::GetJobPageUseCase)

    viewModelOf(::WorkersPaymentViewModel)
    factoryOf(::GetWorkersPaymentInfoUseCase)
    factoryOf(::PayWorkersDebitUseCase)
    factoryOf(::InspectWorkersPaymentTicketUseCase)
    factoryOf(::SubmitInspectionUseCase)
    factoryOf(::GetInspectionReportPDFUseCase)
    viewModelOf(::InspectionViewModel)

    // خدمات غیرحضوری کارفرمایان — use cases (GetUserProfileUseCase, GetEmployerAgreementsUseCase)
    // are already provided by core-domain's DomainModule.
    viewModelOf(::EmployerOnlineServicesViewModel)
}

