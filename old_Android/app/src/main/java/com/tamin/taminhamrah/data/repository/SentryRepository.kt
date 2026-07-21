package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.models.services.SentryConfig
import com.tamin.taminhamrah.data.remote.models.services.SentrySessionReplayConfig
import com.tamin.taminhamrah.data.remote.services.SentryRemoteDataSource
import com.tamin.taminhamrah.di.interceptor.TokenHolder
import io.sentry.ProfileLifecycle
import timber.log.Timber
import javax.inject.Inject


interface SentryConfigStrategy {
    suspend fun fetchConfig(): SentryConfig?
}

    class SentryRepository @Inject constructor(
        private val remoteDataSource: SentryRemoteDataSource,
        private val preferenceManager: PreferenceManager,
    ) {


        suspend fun getSentryConfig(): SentryConfig {
            val strategies = listOf(
                RemoteConfigStrategy(remoteDataSource, preferenceManager),
                LocalConfigStrategy(preferenceManager)
            )

            return strategies.firstNotNullOfOrNull { it.fetchConfig() }
                ?: DEFAULT_CONFIG
        }


        private class RemoteConfigStrategy(
            private val dataSource: SentryRemoteDataSource,
            private val pref: PreferenceManager
        ) : SentryConfigStrategy {
            override suspend fun fetchConfig(): SentryConfig? = try {
                val response = dataSource.getSentryConfig(TokenHolder.getAccessToken(pref))
                if (response?.isSuccessful == true) {
                    response.body()?.also {
                        pref.saveSentryConfig(it)
                    }
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Sentry remote config fetch failed")
                null
            }
        }


        private class LocalConfigStrategy(private val pref: PreferenceManager) : SentryConfigStrategy {
            override suspend fun fetchConfig(): SentryConfig? = pref.getSentryConfig()
        }

        companion object {

            private val DEFAULT_CONFIG = SentryConfig(
                isEnabled = true,
                isSendDefaultPii = true,
                isAttachViewHierarchy = true,
                isAttachScreenshot = true,
                isEnableUserInteractionTracing = true,
                tracesSampleRate = 1.0,
                profilesSampleRate = 1.0,
                sampleRate = 1.0,
                isStartProfilerOnAppStart = true,
                profileLifecycle = ProfileLifecycle.TRACE.toString(),
                sessionReplay = SentrySessionReplayConfig(
                    onErrorSampleRate = 1.0,
                    sessionSampleRate = 1.0,
                )
            )
        }
    }
