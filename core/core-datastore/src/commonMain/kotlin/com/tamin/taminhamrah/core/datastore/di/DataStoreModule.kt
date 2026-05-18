package com.tamin.taminhamrah.core.datastore.di

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.core.datastore.UserPreferencesRepository
import com.tamin.taminhamrah.core.datastore.UserPreferencesRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val datastoreModule = module {
    single<Settings> { Settings() }
    singleOf(::UserPreferencesRepositoryImpl) bind UserPreferencesRepository::class
}
