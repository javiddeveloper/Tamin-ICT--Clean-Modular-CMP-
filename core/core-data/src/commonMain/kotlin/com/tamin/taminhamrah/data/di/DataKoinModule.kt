package com.tamin.taminhamrah.data.di

import com.tamin.taminhamrah.data.feature.FeatureManagerImpl
import com.tamin.taminhamrah.data.repository.UserRepositoryImpl
import com.tamin.taminhamrah.data.repository.CityProvinceRepositoryImpl
import com.tamin.taminhamrah.data.repository.RecipientRepositoryImpl
import com.tamin.taminhamrah.data.repository.agent.AgentChatCacheRepositoryImpl
import com.tamin.taminhamrah.data.repository.common.CommonRepositoryImpl
import com.tamin.taminhamrah.repository.AgentChatCacheRepository
import com.tamin.taminhamrah.data.repository.treatment.TreatmentRepositoryImpl
import com.tamin.taminhamrah.data.repository.personalInbox.PersonalInboxRepositoryImpl
import com.tamin.taminhamrah.data.repository.HistoryRepositoryImpl
import com.tamin.taminhamrah.data.repository.calculateWagePension.CalculateWagePensionRepositoryImpl
import com.tamin.taminhamrah.data.repository.VersionHistoryRepositoryImpl
import com.tamin.taminhamrah.data.repository.contract.ContractsRepositoryImpl
import com.tamin.taminhamrah.data.repository.pension.PensionRepositoryImpl
import com.tamin.taminhamrah.data.repository.userRequests.UserRequestRepositoryImpl
import com.tamin.taminhamrah.data.repository.orotezProtez.OrotezProtezRepositoryImpl
import com.tamin.taminhamrah.data.repository.personal.PersonalRepositoryImpl
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.RecipientRepository
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.calculateWagePension.CalculateWagePensionRepository
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.repository.orotezProtez.OrotezProtezRepository
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import com.tamin.taminhamrah.repository.health.HealthRepository
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import com.tamin.taminhamrah.repository.VersionHistoryRepository
import com.tamin.taminhamrah.data.repository.ContactUsRepositoryImpl
import com.tamin.taminhamrah.repository.ContactUsRepository
import com.tamin.taminhamrah.data.repository.health.HealthRepositoryImpl
import com.tamin.taminhamrah.data.repository.addDependent.AddDependentRepositoryImpl
import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.repository.WorkShopsRepository
import com.tamin.taminhamrah.data.repository.InspectionRepositoryImpl
import com.tamin.taminhamrah.repository.inspection.InspectionRepository
import com.tamin.taminhamrah.data.repository.occurrence.OccurrenceRepositoryImpl
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository
import com.tamin.taminhamrah.data.repository.historyObjection.HistoryObjectionRepositoryImpl
import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import com.tamin.taminhamrah.data.repository.employerInfo.EmployerInfoRepositoryImpl
import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataKoinModule = module {
    singleOf(::FeatureManagerImpl) { bind<FeatureManager>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::CityProvinceRepositoryImpl) { bind<CityProvinceRepository>() }
    singleOf(::RecipientRepositoryImpl) { bind<RecipientRepository>() }

    singleOf(::TreatmentRepositoryImpl) { bind<TreatmentRepository>() }
    singleOf(::PensionRepositoryImpl) { bind<PensionRepository>() }
    singleOf(::HistoryRepositoryImpl) { bind<HistoryRepository>() }
    singleOf(::CalculateWagePensionRepositoryImpl) { bind<CalculateWagePensionRepository>() }
    singleOf(::CommonRepositoryImpl) { bind<CommonRepository>() }
    singleOf(::AgentChatCacheRepositoryImpl) { bind<AgentChatCacheRepository>() }
    singleOf(::WorkShopsRepositoryImpl) { bind<WorkShopsRepository>() }
    singleOf(::PersonalInboxRepositoryImpl) { bind<PersonalInboxRepository>() }
    singleOf(::UserRequestRepositoryImpl) { bind<UserRequestRepository>() }
    singleOf(::PersonalRepositoryImpl) { bind<PersonalRepository>() }
    singleOf(::ContractsRepositoryImpl) { bind<ContractsRepository>() }
    singleOf(::HealthRepositoryImpl) { bind<HealthRepository>() }
    singleOf(::AddDependentRepositoryImpl) { bind<AddDependentRepository>() }
    singleOf(::VersionHistoryRepositoryImpl) { bind<VersionHistoryRepository>() }
    singleOf(::ContactUsRepositoryImpl) { bind<ContactUsRepository>() }
    singleOf(::OrotezProtezRepositoryImpl) { bind<OrotezProtezRepository>() }
    singleOf(::OccurrenceRepositoryImpl) { bind<OccurrenceRepository>() }
    singleOf(::InspectionRepositoryImpl) { bind<InspectionRepository>() }
    singleOf(::HistoryObjectionRepositoryImpl) { bind<HistoryObjectionRepository>() }
    singleOf(::EmployerInfoRepositoryImpl) { bind<EmployerInfoRepository>() }
}
