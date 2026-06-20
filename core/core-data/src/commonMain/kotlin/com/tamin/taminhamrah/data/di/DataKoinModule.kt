package com.tamin.taminhamrah.data.di

import com.tamin.taminhamrah.data.repository.UserRepositoryImpl
import com.tamin.taminhamrah.data.repository.CityProvinceRepositoryImpl
import com.tamin.taminhamrah.data.repository.RecipientRepositoryImpl
import com.tamin.taminhamrah.data.repository.common.CommonRepositoryImpl
import com.tamin.taminhamrah.data.repository.inbox.PersonalInboxRepositoryImpl
import com.tamin.taminhamrah.data.repository.pension.PensionRepositoryImpl
import com.tamin.taminhamrah.data.repository.request.RequestRepositoryImpl
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.RecipientRepository
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.inbox.PersonalInboxRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.repository.request.RequestRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataKoinModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::CityProvinceRepositoryImpl) { bind<CityProvinceRepository>() }
    singleOf(::RecipientRepositoryImpl) { bind<RecipientRepository>() }
    singleOf(::PensionRepositoryImpl) { bind<PensionRepository>() }
    singleOf(::CommonRepositoryImpl) { bind<CommonRepository>() }
    singleOf(::RequestRepositoryImpl) { bind<RequestRepository>() }
    singleOf(::PersonalInboxRepositoryImpl) { bind<PersonalInboxRepository>() }
}
