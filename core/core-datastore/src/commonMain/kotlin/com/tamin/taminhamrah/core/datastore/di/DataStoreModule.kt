package com.tamin.taminhamrah.core.datastore.di

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.core.datastore.UserPreferencesRepositoryImpl
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.core.datastore.token.TokenStoreManagerImpl
import com.tamin.taminhamrah.repository.BiometricSessionState
import com.tamin.taminhamrah.core.datastore.InMemoryBiometricSessionState
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.core.datastore.DeveloperOptionsRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val datastoreModule = module {
    single<Settings> { Settings() }
    singleOf(::UserPreferencesRepositoryImpl) bind UserPreferencesRepository::class
    singleOf(::TokenStoreManagerImpl) bind TokenStoreManager::class
    singleOf(::InMemoryBiometricSessionState) bind BiometricSessionState::class
    singleOf(::DeveloperOptionsRepositoryImpl) bind DeveloperOptionsRepository::class
}
