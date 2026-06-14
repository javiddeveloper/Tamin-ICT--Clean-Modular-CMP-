package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.core.datastore.di.datastoreModule
import com.tamin.taminhamrah.data.di.dataKoinModule
import com.tamin.taminhamrah.feature.profile.di.profileModule
import com.tamin.taminhamrah.plugin.di.pluginModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

val sharedModules: List<Module>
    get() = listOf(
        platformModule,
        networkModule,
        datastoreModule,
        databaseModule,
        ApiClientsModule,
        remoteModule,
        domainModule,
        dataKoinModule,
        dataModule,
        pluginModule,
        profileModule,
    )

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModules)
    }
}
