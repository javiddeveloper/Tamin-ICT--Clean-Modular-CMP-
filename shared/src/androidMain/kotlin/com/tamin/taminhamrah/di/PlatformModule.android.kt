package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.data.local.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { getDatabaseBuilder(get()) }
//    single<NetworkRepository> { AndroidNetworkMonitor(get()) }
}
