package com.tamin.taminhamrah.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.tamin.taminhamrah.data.local.getDatabaseBuilder
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { getDatabaseBuilder(get()) }

    single<HttpClientEngine> {
        OkHttp.create {
            config {
                addInterceptor(ChuckerInterceptor.Builder(androidContext()).build())
            }
        }
    }
}
