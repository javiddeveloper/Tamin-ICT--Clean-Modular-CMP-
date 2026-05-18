package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.ui.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    viewModelOf(::MainViewModel)
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
