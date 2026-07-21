package com.tamin.taminhamrah

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.tamin.taminhamrah.data.remote.HeaderInjectingTransportFactory
import com.google.android.material.color.DynamicColors
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.SentryConfig
import com.tamin.taminhamrah.data.repository.SentryRepository
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.HiltAndroidApp
import io.sentry.ProfileLifecycle
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import org.acra.config.httpSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.acra.sender.HttpSender
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class AppController : Application(), ImageLoaderFactory {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var sentryRepository: SentryRepository
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        val isInitialized = initSentryWithCachedConfig()
        appScope.launch {
            runCatching {
                val newConfig = sentryRepository.getSentryConfig()
                if (!isInitialized && newConfig.isEnabled == true) {
                     withContext(Dispatchers.Main) {
                         SentryAndroid.init(this@AppController) { options ->
                             configureSentry(options, newConfig)
                         }
                     }
                }
            }
        }

        DynamicColors.applyToActivitiesIfAvailable(this)
        if (!BuildConfig.DEBUG) {
            System.setProperty(
                kotlinx.coroutines.DEBUG_PROPERTY_NAME,
                kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
            )
        }
    }

    private fun initSentryWithCachedConfig(): Boolean {
        try {
            val cachedConfig = preferenceManager.getSentryConfig()
            
            if (cachedConfig != null && cachedConfig.isEnabled == true) {
                 SentryAndroid.init(this) { options ->
                     configureSentry(options, cachedConfig)
                 }
                 return true
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to init Sentry synchronously")
        }
        return false
    }

    private fun configureSentry(options: SentryAndroidOptions, sentryConfig: SentryConfig) {
        val customTransportFactory = HeaderInjectingTransportFactory(
            BuildConfig.SENTRY_AUTH_HEADER,
            preferenceManager
        )
        options.apply {
            dsn = BuildConfig.SENTRY_DSN
            isDebug = BuildConfig.DEBUG
            setTransportFactory(customTransportFactory)

        }

        options.apply {
            sentryConfig.isSendDefaultPii?.also { isSendDefaultPii = it }
            sentryConfig.isAttachViewHierarchy?.also { isAttachViewHierarchy = it }
            sentryConfig.isStartProfilerOnAppStart?.also { isStartProfilerOnAppStart = it }
            sentryConfig.isAttachScreenshot?.also { isAttachScreenshot = it }
            sentryConfig.isEnableUserInteractionTracing?.also {
                isEnableUserInteractionTracing = it
            }
            sentryConfig.tracesSampleRate?.also { tracesSampleRate = it }
            sentryConfig.profilesSampleRate?.also { profilesSampleRate = it }
            sentryConfig.sampleRate?.also { sampleRate = it }

            sentryConfig.profileLifecycle.toProfileLifecycle()?.also {
                profileLifecycle = it
            }

            sentryConfig.sessionReplay?.also { replayConfig ->
                replayConfig.sessionSampleRate?.also { sessionReplay.sessionSampleRate = it }
                replayConfig.onErrorSampleRate?.also { sessionReplay.onErrorSampleRate = it }
            }

        }
    }

    fun String?.toProfileLifecycle(): ProfileLifecycle? {
        return when (this?.uppercase()) {
            "TRACE" -> ProfileLifecycle.TRACE
            "MANUAL" -> ProfileLifecycle.MANUAL
            else -> null
        }
    }

    private fun setThemePreference(applicationTheme: ApplicationThemeEnum) {
        when (applicationTheme) {
            ApplicationThemeEnum.FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )

            ApplicationThemeEnum.NIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ApplicationThemeEnum.DAY -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun attachBaseContext(base: Context) {
        if (BuildConfig.DEBUG)
            plant(Timber.DebugTree())

        super.attachBaseContext(base)
        val sharedPreferences: SharedPreferences? =
            base.getSharedPreferences("com.tamin.taminhamrah", MODE_PRIVATE)


        setThemePreference(getCurrentTheme(sharedPreferences))

        val prefConfig = sharedPreferences?.getString(
            Constants.ACRA_CONFIG_TAG, base.assets
                ?.open("acra_config.txt")
                ?.bufferedReader()
                ?.use { it.readText() })

        val json = JsonParser().parse(prefConfig).asJsonObject

        val result = Gson().fromJson(json, AcraConfigResponse::class.java)

        Timber.tag("acraConfigResponse").i("AppClass :config=${result.data}")

        if (!Utility.isDebuggable(base))
            initAcra {
                buildConfigClass = BuildConfig::class.java
                reportFormat = StringFormat.JSON
                httpSender {
                    uri = "https://acrarium.tamin.ir${result.data?.uri}"
                    basicAuthLogin = result.data?.basicAuthLogin ?: ""
                    basicAuthPassword = result.data?.basicAuthPassword ?: ""
                    httpMethod = HttpSender.Method.POST
                }
            }
    }

    private fun getCurrentTheme(sharedPreferences: SharedPreferences?): ApplicationThemeEnum {
        return when (sharedPreferences?.getInt(
            Constants.APPLICATION_THEME,
            ApplicationThemeEnum.FOLLOW_SYSTEM.state
        ) ?: ApplicationThemeEnum.FOLLOW_SYSTEM.state) {
            0 -> {
                ApplicationThemeEnum.FOLLOW_SYSTEM
            }

            1 -> {
                ApplicationThemeEnum.DAY
            }

            2 -> {
                ApplicationThemeEnum.NIGHT
            }

            else -> {
                ApplicationThemeEnum.FOLLOW_SYSTEM
            }
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .crossfade(true) // Show a short crossfade when loading images from network or disk.
            .components {
                add(SvgDecoder.Factory())
            }
            .okHttpClient {

                val cacheDirectory = File(filesDir, "image_cache").apply { mkdirs() }
                val cache = Cache(cacheDirectory, Long.MAX_VALUE)


                val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                OkHttpClient.Builder()
                    .cache(cache)
                    .dispatcher(dispatcher)
                    .build()
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
