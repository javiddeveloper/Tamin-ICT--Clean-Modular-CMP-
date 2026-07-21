package com.tamin.taminhamrah.di.interceptor

import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.user.UserService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider

class RequestInterceptor @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val userServiceProvider: Provider<UserService>
) : Interceptor {

    @Volatile
    private var isRefreshing = false

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestHasAuth = hasAuth(originalRequest)
        val skipTokenRefresh = originalRequest.url.encodedPath.endsWith("token")

        if (!skipTokenRefresh) {
            if (requestHasAuth) {
                synchronized(this) {
                    val now = nowSeconds()
                    val expireTime = TokenHolder.getExpiresAt(preferenceManager)
                    if (shouldRefresh(expireTime, now)) {
                        if (!isRefreshing) {
                            isRefreshing = true
                            try {
                                val updatedRequest = refreshAndRewrite(originalRequest)
                                if (updatedRequest != null) return chain.proceed(updatedRequest)
                            } catch (_: Exception) {
                            } finally {
                                isRefreshing = false
                            }
                        }
                    }
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Timber.tag("NetworkInterceptor").i(
                "header :${originalRequest.headers} \n body:${originalRequest.body}\n requesr=$originalRequest",
            )
        }
        return chain.proceed(originalRequest)
    }

    private fun hasAuth(request: Request) = request.header(Constants.AUTHENTICATION) != null

    private fun nowSeconds() = System.currentTimeMillis() / 1000

    private fun shouldRefresh(expireTime: Long, now: Long) =
        expireTime > 0 && now >= expireTime - 5

    private fun refreshAndRewrite(originalRequest: Request): Request? {
        val refreshToken = TokenHolder.getRefreshToken(preferenceManager) ?: return null
        val call = userServiceProvider.get().refreshToken(refreshToken = refreshToken)
        val tokenResponse = call.execute()
        if (!tokenResponse.isSuccessful || tokenResponse.body() == null) return null
        val body = tokenResponse.body()!!
        val newAccessToken = "Bearer ${body.accessToken}"
        val newExpire = nowSeconds() + body.expiresIn
        TokenHolder.updateTokens(preferenceManager, newAccessToken, body.refreshToken, newExpire)
        if (BuildConfig.DEBUG) {
            Timber.tag("RequestInterceptor").i("Token refreshed proactively")
        }
        return originalRequest.newBuilder()
            .header(Constants.AUTHENTICATION, newAccessToken)
            .build()
    }
}
