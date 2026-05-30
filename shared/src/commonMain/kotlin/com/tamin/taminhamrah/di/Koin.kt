package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.core.datastore.di.datastoreModule
import com.tamin.taminhamrah.feature.cityprovince.di.cityProvinceModule
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
        dataModule,
        pluginModule,
        cityProvinceModule,
        profileModule,
    )

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModules)
    }
}
