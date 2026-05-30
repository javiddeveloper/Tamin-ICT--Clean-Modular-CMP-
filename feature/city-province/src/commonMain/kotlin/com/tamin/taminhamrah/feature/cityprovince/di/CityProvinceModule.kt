package com.tamin.taminhamrah.feature.cityprovince.di

import com.tamin.taminhamrah.feature.cityprovince.data.repository.CityProvinceRepositoryImpl
import com.tamin.taminhamrah.repository.CityProvinceRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val cityProvinceModule = module {
    singleOf(::CityProvinceRepositoryImpl) { bind<CityProvinceRepository>() }
}
