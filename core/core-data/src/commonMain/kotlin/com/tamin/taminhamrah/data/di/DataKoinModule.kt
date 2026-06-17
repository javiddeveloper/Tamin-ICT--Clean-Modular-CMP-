package com.tamin.taminhamrah.data.di

import com.tamin.taminhamrah.data.repository.UserRepositoryImpl
import com.tamin.taminhamrah.data.repository.CityProvinceRepositoryImpl
import com.tamin.taminhamrah.data.repository.HistoryRepositoryImpl
import com.tamin.taminhamrah.data.repository.pension.PensionRepositoryImpl
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataKoinModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::CityProvinceRepositoryImpl) { bind<CityProvinceRepository>() }
    singleOf(::PensionRepositoryImpl) { bind<PensionRepository>() }
    singleOf(::HistoryRepositoryImpl) { bind<HistoryRepository>() }
}
