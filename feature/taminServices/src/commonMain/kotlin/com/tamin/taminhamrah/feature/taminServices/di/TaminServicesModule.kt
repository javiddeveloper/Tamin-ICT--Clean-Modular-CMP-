package com.tamin.taminhamrah.feature.taminServices.di

import com.tamin.taminhamrah.feature.taminServices.occurrence.OccurrenceViewModel
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.SendHistoryToInstitutionsViewModel
import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
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
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val TaminServicesModule = module {
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
    factoryOf(::SubmitInspectionUseCase)
    factoryOf(::GetInspectionReportPDFUseCase)
    viewModelOf(::InspectionViewModel)
}

