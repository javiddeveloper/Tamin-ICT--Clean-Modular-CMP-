package com.tamin.taminhamrah

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.tamin.taminhamrah.di.initKoin
import com.tamin.taminhamrah.ui.image.taminImageLoader
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import org.koin.dsl.module

class TaminHamrahApplication : Application(), SingletonImageLoader.Factory, KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TaminHamrahApplication)
            modules(androidModule)
        }
    }

    override fun newImageLoader(context: coil3.PlatformContext): ImageLoader {
        val httpClient: HttpClient? = getKoin().getOrNull<HttpClient>(named("mainHttpClient")) ?: getKoin().getOrNull<HttpClient>()
        return taminImageLoader(context, httpClient)
    }
}

val androidModule = module {
    // Add your android specific dependencies here
}
