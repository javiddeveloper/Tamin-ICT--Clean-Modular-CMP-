package com.tamin.taminhamrah.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    // Repositories
//    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // UseCase
//    factory { ExportWithPluginUseCase(get()) }

    // Theme plugins
//    factory { ImportThemeUseCase(get(), get()) }

    // Backup DAO wrappers
//    single { PreferenceDaos(get(), get(), get()) }
//    single { UserDaos(get(), get(), get()) }
}
