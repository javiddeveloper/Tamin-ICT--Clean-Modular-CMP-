package com.tamin.taminhamrah.data.di

import com.tamin.taminhamrah.data.feature.FeatureManagerImpl
import com.tamin.taminhamrah.data.repository.UserRepositoryImpl
import com.tamin.taminhamrah.data.repository.CityProvinceRepositoryImpl
import com.tamin.taminhamrah.data.repository.RecipientRepositoryImpl
import com.tamin.taminhamrah.data.repository.common.CommonRepositoryImpl
import com.tamin.taminhamrah.data.repository.treatment.TreatmentRepositoryImpl
import com.tamin.taminhamrah.data.repository.personalInbox.PersonalInboxRepositoryImpl
import com.tamin.taminhamrah.data.repository.HistoryRepositoryImpl
import com.tamin.taminhamrah.data.repository.contract.ContractsRepositoryImpl
import com.tamin.taminhamrah.data.repository.pension.PensionRepositoryImpl
import com.tamin.taminhamrah.data.repository.userRequests.UserRequestRepositoryImpl
import com.tamin.taminhamrah.data.repository.personal.PersonalRepositoryImpl
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.RecipientRepository
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import com.tamin.taminhamrah.repository.health.HealthRepository
import com.tamin.taminhamrah.data.repository.health.HealthRepositoryImpl
import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.repository.WorkShopsRepository
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
    singleOf(::CommonRepositoryImpl) { bind<CommonRepository>() }
    singleOf(::WorkShopsRepositoryImpl) { bind<WorkShopsRepository>() }
    singleOf(::PersonalInboxRepositoryImpl) { bind<PersonalInboxRepository>() }
    singleOf(::UserRequestRepositoryImpl) { bind<UserRequestRepository>() }
    singleOf(::PersonalRepositoryImpl) { bind<PersonalRepository>() }
    singleOf(::ContractsRepositoryImpl) { bind<ContractsRepository>() }
    single<HealthRepository> { HealthRepositoryImpl(get(), get()) }
}
