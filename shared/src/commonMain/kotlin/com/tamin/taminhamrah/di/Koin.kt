package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.core.datastore.di.datastoreModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

val sharedModules: List<Module>
    get() = listOf(
        platformModule,
        networkModule,
        databaseModule,
        dataModule,
        domainModule,
        datastoreModule,
    )

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModules)
    }
}
