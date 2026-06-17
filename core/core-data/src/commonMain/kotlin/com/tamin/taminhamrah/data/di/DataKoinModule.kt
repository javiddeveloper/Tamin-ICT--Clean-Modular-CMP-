package com.tamin.taminhamrah.data.di

import com.tamin.taminhamrah.data.repository.UserRepositoryImpl
import com.tamin.taminhamrah.data.repository.CityProvinceRepositoryImpl
import com.tamin.taminhamrah.data.repository.RecipientRepositoryImpl
import com.tamin.taminhamrah.data.repository.pension.PensionRepositoryImpl
import com.tamin.taminhamrah.data.repository.personal.PersonalRepositoryImpl
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.RecipientRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataKoinModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::CityProvinceRepositoryImpl) { bind<CityProvinceRepository>() }
    singleOf(::RecipientRepositoryImpl) { bind<RecipientRepository>() }
    singleOf(::PensionRepositoryImpl) { bind<PensionRepository>() }
    singleOf(::PersonalRepositoryImpl) { bind<PersonalRepository>() }
}
